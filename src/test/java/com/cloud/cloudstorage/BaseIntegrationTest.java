package com.cloud.cloudstorage;

import jakarta.transaction.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Transactional
@Testcontainers
public abstract class BaseIntegrationTest {
    @Container
    @ServiceConnection
    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgreSQLContainer= new PostgreSQLContainer<>("postgres")
            .withDatabaseName("cloud_storage_test")
            .withUsername("test_user")
            .withPassword("test_password");

    @Container
    private static final MinIOContainer minioContainer = new MinIOContainer("minio/minio")
            .withUserName("testaccesskey")
            .withPassword("testsecretkey")
            .withCommand("server /data --console-address :9090");

    @DynamicPropertySource
    private static void setMinioProperties(DynamicPropertyRegistry registry) {
        registry.add("minio.url", () -> String.format("http://%s:%d",
                minioContainer.getHost(),
                minioContainer.getMappedPort(9000)));
        registry.add("minio.accessKey", () -> "testaccesskey");
        registry.add("minio.secretKey", () -> "testsecretkey");
        registry.add("minio.bucket", () -> "test-cloud-storage-bucket");

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.liquibase.enabled", () -> "true");
    }
}
