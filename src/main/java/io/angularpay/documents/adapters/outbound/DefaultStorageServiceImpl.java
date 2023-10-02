package io.angularpay.documents.adapters.outbound;

import io.angularpay.documents.configurations.AngularPayConfiguration;
import io.angularpay.documents.exceptions.CommandException;
import io.angularpay.documents.models.DownloadDocumentApiResponse;
import io.angularpay.documents.ports.outbound.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.angularpay.documents.exceptions.ErrorCode.FILE_IO_ERROR;

@Service
@RequiredArgsConstructor
public class DefaultStorageServiceImpl implements StorageService {

    private final AngularPayConfiguration configuration;

    @Override
    public void saveFile(String documentReference, String ownerReference, byte[] file) throws IOException {
        try {
            Path userDocumentsDirectory = Paths.get(this.configuration.getDocumentsRoot(), ownerReference);
            Path userDocumentFilePath = Paths.get(this.configuration.getDocumentsRoot(), ownerReference, documentReference);
            Files.createDirectories(userDocumentsDirectory);
            Files.write(userDocumentFilePath, file);
        } catch (IOException exception) {
            throw CommandException.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(FILE_IO_ERROR)
                    .message(FILE_IO_ERROR.getDefaultMessage())
                    .cause(exception)
                    .build();
        }
    }

    @Override
    public DownloadDocumentApiResponse loadFile(String documentReference, String ownerReference) throws IOException {
        try {
            Path userDocumentFilePath = Paths.get(this.configuration.getDocumentsRoot(), ownerReference, documentReference);
            File file = userDocumentFilePath.toFile();
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return DownloadDocumentApiResponse.builder()
                    .resource(resource)
                    .contentLength(file.length())
                    .build();
        } catch (IOException exception) {
            throw CommandException.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(FILE_IO_ERROR)
                    .message(FILE_IO_ERROR.getDefaultMessage())
                    .cause(exception)
                    .build();
        }
    }

    @Override
    public void deleteFile(String documentReference, String ownerReference) throws IOException {
        try {
            Path userDocumentsDirectory = Paths.get(this.configuration.getDocumentsRoot(), ownerReference);
            Path userDocumentFilePath = Paths.get(this.configuration.getDocumentsRoot(), ownerReference, documentReference);
            Files.createDirectories(userDocumentsDirectory);
            Files.delete(userDocumentFilePath);
        } catch (IOException exception) {
            throw CommandException.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(FILE_IO_ERROR)
                    .message(FILE_IO_ERROR.getDefaultMessage())
                    .cause(exception)
                    .build();
        }
    }
}
