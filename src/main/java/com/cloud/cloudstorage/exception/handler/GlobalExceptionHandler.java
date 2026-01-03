package com.cloud.cloudstorage.exception.handler;

import com.cloud.cloudstorage.dto.ErrorResponseDto;
import com.cloud.cloudstorage.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistException(
            UserAlreadyExistException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.CONFLICT,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleEmailAlreadyExistException(
            EmailAlreadyExistException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.CONFLICT,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        String errors = String.join(", ", messages);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                errors,
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponseDto);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {

        String message = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("Invalid request");


        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                message,
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponseDto);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex,
            HttpServletRequest request
    ) {
        Optional<String> errorMessage = ex.getAllErrors().stream()
                .map(error -> {
                    if (error instanceof MessageSourceResolvable resolvable) {
                        return Optional.ofNullable(resolvable.getDefaultMessage())
                                .orElse("Validation failed");
                    }
                    return "Validation failed";
                })
                .findFirst();

        String message = errorMessage.orElse("Validation failed");

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                message,
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponseDto);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUsernameNotFoundException(
            UsernameNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                "Invalid username or password",
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MinioBucketInitializationException.class)
    public ResponseEntity<ErrorResponseDto> handleMinioBucketInitializationException(
            MinioBucketInitializationException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CreateRootMinioDirectoryException.class)
    public ResponseEntity<ErrorResponseDto> handleCreateRootMinioDirectoryException(
            CreateRootMinioDirectoryException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MinioCreatingDirectoryException.class)
    public ResponseEntity<ErrorResponseDto> handleMinioCreatingDirectoryException(
            MinioCreatingDirectoryException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MinioExistingParentDirectoryException.class)
    public ResponseEntity<ErrorResponseDto> handleMinioExistingParentDirectoryException(
            MinioExistingParentDirectoryException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MinioResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleMinioDirectoryAlreadyExistsException(
            MinioResourceAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.CONFLICT,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MinioUploadingResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleMinioUploadingResourceAlreadyExistsException(
            MinioUploadingResourceAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.CONFLICT,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MinioResourceNotExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleMinioDirectoryNotExistsException(
            MinioResourceNotExistsException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseDto);
    }

    @ExceptionHandler(MinioUploadException.class)
    public ResponseEntity<ErrorResponseDto> handleMinioUploadException(
            MinioUploadException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MinioMovingException.class)
    public ResponseEntity<ErrorResponseDto> handleMinioMovingException(
            MinioMovingException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MinioGettingDirectoryContentException.class)
    public ResponseEntity<ErrorResponseDto> handleMinioGettingDirectoryContentException(
            MinioGettingDirectoryContentException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MinioGetObjectSizeException.class)
    public ResponseEntity<ErrorResponseDto> handleMinioGetObjectSizeException(
            MinioGetObjectSizeException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MinioDownloadResourceException.class)
    public ResponseEntity<ErrorResponseDto> handleMinioDownloadResourceException(
            MinioDownloadResourceException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseDto);
    }

    @ExceptionHandler(MinioTypesNotMatchException.class)
    public ResponseEntity<ErrorResponseDto> handleMinioTypesNotMatchException(
            MinioTypesNotMatchException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(
            HttpServletRequest request
    ) {
        ErrorResponseDto responseDto = new ErrorResponseDto(
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
