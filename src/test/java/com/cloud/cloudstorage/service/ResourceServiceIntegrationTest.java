package com.cloud.cloudstorage.service;

import com.cloud.cloudstorage.BaseIntegrationTest;
import com.cloud.cloudstorage.dto.BaseResourceResponseDto;
import com.cloud.cloudstorage.dto.FileUploadDto;
import com.cloud.cloudstorage.dto.StreamResourceDto;
import com.cloud.cloudstorage.dto.UserCreateDto;
import com.cloud.cloudstorage.dto.enums.ResourceType;
import com.cloud.cloudstorage.exception.MinioResourceNotExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.annotation.DirtiesContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ResourceServiceIntegrationTest extends BaseIntegrationTest {
    private static final String DIRECTORY_PATH = "test-dir/";

   @Autowired
   private ResourceService resourceService;

   @Autowired
   private PathBuilderService pathBuilderService;

   @Autowired
   private PathFormatterService pathFormatterService;

   @Autowired
   private UserAccountService userAccountService;

   @Autowired
   private CurrentUserService currentUserService;

   private User user;

    @BeforeEach
    void setUp() {
        registerNewUser();
        user = createUserDetails();
    }

    @Test
    void shouldCreateEmptyDirectory() {
        resourceService.createEmptyDirectory(DIRECTORY_PATH, user);
        String fullPath = getFullPath(DIRECTORY_PATH);
        boolean result = resourceService.isResourceExisting(fullPath);
        assertThat(result).isTrue();
    }

    @Test
    void shouldGetDirectoryContent() {
        String nestedFolder = "test-dir2/";
        resourceService.createEmptyDirectory(DIRECTORY_PATH, user);
        resourceService.createEmptyDirectory(DIRECTORY_PATH + nestedFolder, user);
        List<BaseResourceResponseDto> directoryContent = resourceService.getDirectoryContent(DIRECTORY_PATH, user);
        BaseResourceResponseDto result = directoryContent.getFirst();
        assertThat(result.getPath()).isEqualTo(DIRECTORY_PATH);
        String responseName = pathFormatterService.formatDirectoryNameForResponse(nestedFolder);
        assertThat(result.getName()).isEqualTo(responseName);
        assertThat(result.getType()).isEqualTo(ResourceType.DIRECTORY);
    }

    @Test
    void shouldUploadFile() {
        String content = "Test file";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                content.getBytes()
        );

        FileUploadDto fileUploadDto = new FileUploadDto(List.of(file));
        resourceService.createEmptyDirectory(DIRECTORY_PATH, user);
        resourceService.uploadFiles(DIRECTORY_PATH, fileUploadDto, user);
        String fullPath = getFullPath(DIRECTORY_PATH + file.getOriginalFilename());
        boolean resourceExisting = resourceService.isResourceExisting(fullPath);
        assertThat(resourceExisting).isTrue();
    }

    @Test
    void shouldUploadFileWithDirectoryInName() {
        String content = "Test file with directory";
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "dir1/dir2/file.txt",
                "text/plain",
                content.getBytes()
        );

        FileUploadDto fileUploadDto = new FileUploadDto(List.of(file));
        resourceService.createEmptyDirectory(DIRECTORY_PATH, user);
        resourceService.uploadFiles(DIRECTORY_PATH, fileUploadDto, user);
        String fullPath = getFullPath(DIRECTORY_PATH + file.getOriginalFilename());
        boolean exists = resourceService.isResourceExisting(fullPath);
        assertThat(exists).isTrue();
    }

    @Test
    void shouldFindResource() {
        resourceService.createEmptyDirectory(DIRECTORY_PATH, user);
        List<BaseResourceResponseDto> searchedContent = resourceService.getSearchedContent(DIRECTORY_PATH, user);
        BaseResourceResponseDto result = searchedContent.getFirst();
        String responseName = pathFormatterService.formatDirectoryNameForResponse(DIRECTORY_PATH);
        assertThat(result.getName()).isEqualTo(responseName);
        assertThat(result.getType()).isEqualTo(ResourceType.DIRECTORY);
    }

    @Test
    void shouldRenameResource() {
        String newDirPath = "test-dir2/";
        resourceService.createEmptyDirectory(DIRECTORY_PATH, user);
        resourceService.moveResource(DIRECTORY_PATH, newDirPath, user);
        String oldPath = getFullPath(DIRECTORY_PATH);
        boolean oldResourcePathExists = resourceService.isResourceExisting(oldPath);
        assertThat(oldResourcePathExists).isFalse();
        String newPath = getFullPath(newDirPath);
        boolean newResourcePathExists = resourceService.isResourceExisting(newPath);
        assertThat(newResourcePathExists).isTrue();
    }

    @Test
    void shouldDownloadFile() throws IOException {
        String content = "Test";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                content.getBytes()
        );

        FileUploadDto fileUploadDto = new FileUploadDto(List.of(file));
        resourceService.createEmptyDirectory(DIRECTORY_PATH, user);
        resourceService.uploadFiles(DIRECTORY_PATH, fileUploadDto, user);

        StreamResourceDto result = resourceService.downloadResource(DIRECTORY_PATH + file.getOriginalFilename(), user);

        assertThat(result.filename()).isEqualTo(file.getOriginalFilename());
        OutputStream outputStream = new ByteArrayOutputStream();
        result.body().writeTo(outputStream);

        String responseContent = outputStream.toString();
        assertThat(responseContent).isEqualTo(content);
    }

    @Test
    void shouldDeleteResource() {
        resourceService.createEmptyDirectory(DIRECTORY_PATH, user);

        resourceService.deleteResource(DIRECTORY_PATH, user);
        String fullPath = getFullPath(DIRECTORY_PATH);
        boolean resourceExisting = resourceService.isResourceExisting(fullPath);

        assertThat(resourceExisting).isFalse();
    }

    @Test
    void shouldGetResourceDetails() {
        resourceService.createEmptyDirectory(DIRECTORY_PATH, user);
        BaseResourceResponseDto result = resourceService.getResourceInfo(DIRECTORY_PATH, user);

        String responseDirName = pathFormatterService.formatDirectoryNameForResponse(DIRECTORY_PATH);
        assertThat(result.getName()).isEqualTo(responseDirName);
        assertThat(result.getType()).isEqualTo(ResourceType.DIRECTORY);
    }

    @Test
    void shouldNotAllowCurrentUserHaveAccessToAnotherUsersFiles() {
        UserCreateDto anotherUserCreateDto = new UserCreateDto("test-user2", "test-password2", "test-email2@gmail.com");
        userAccountService.registerNewUser(anotherUserCreateDto, new MockHttpServletRequest());

        User anotherUser = new User("test-user2", "test-password2", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        resourceService.createEmptyDirectory(DIRECTORY_PATH, user);

        assertThrows(
                MinioResourceNotExistsException.class,
                () -> resourceService.getResourceInfo(DIRECTORY_PATH, anotherUser));
    }

    @Test
    void shouldAllowCurrentUserSearchOnlyHisResources() {
        UserCreateDto anotherUserCreateDto = new UserCreateDto("test-user2", "test-password2", "test-email2@gmail.com");
        userAccountService.registerNewUser(anotherUserCreateDto, new MockHttpServletRequest());

        User anotherUser = new User("test-user2", "test-password2", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        resourceService.createEmptyDirectory(DIRECTORY_PATH, user);

        List<BaseResourceResponseDto> result = resourceService.getSearchedContent(DIRECTORY_PATH, anotherUser);

        assertThatList(result).isEmpty();

    }

    private void registerNewUser() {
        UserCreateDto userCreateDto = new UserCreateDto("test-user", "test-password", "test-email@gmail.com");
        userAccountService.registerNewUser(userCreateDto, new MockHttpServletRequest());
    }

    private User createUserDetails() {
        return new User("test-user", "test-password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private String getFullPath(String path) {
        Long userId = currentUserService.getCurrentUserId(user);
        return pathBuilderService.createFullDirectoryPath(userId, path);
    }

}
