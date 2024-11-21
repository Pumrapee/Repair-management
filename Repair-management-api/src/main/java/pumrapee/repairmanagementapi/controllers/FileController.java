package pumrapee.repairmanagementapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pumrapee.repairmanagementapi.entities.File;
import pumrapee.repairmanagementapi.exceptions.BadRequestException;
import pumrapee.repairmanagementapi.exceptions.IOExceptionHandler;
import pumrapee.repairmanagementapi.services.FileService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@RestController
@CrossOrigin(origins = {"http://localhost:5173"})
@RequestMapping("/orders/{orderId}/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@PathVariable Integer orderId, @RequestParam("file") MultipartFile file) {
        String filePath = fileService.saveFile(orderId, file);
        return ResponseEntity.ok("File uploaded successfully: " + filePath);
    }

    @GetMapping
    public ResponseEntity<List<File>> getFile(@PathVariable Integer orderId) {
        return ResponseEntity.ok(fileService.getFileByOrder(orderId));
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<?> downloadFile(@PathVariable Integer orderId ,@PathVariable Integer fileId) {
        if (fileService.getFileByOrder(orderId).stream().noneMatch(file -> Objects.equals(file.getId(), fileId))) {
            throw new BadRequestException("File with id " + fileId + " not exists in order " + orderId);
        }
        File file = fileService.getFileById(fileId);
        Path filePath = Paths.get(file.getFilePath());
        UrlResource resource;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IOExceptionHandler("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new IOExceptionHandler("Error while reading the file: " + e.getMessage());
        }

        String contentType;
        try {
            contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
        } catch (IOException e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Integer orderId, @PathVariable Integer fileId) {
        if (fileService.getFileByOrder(orderId).stream().noneMatch(file -> Objects.equals(file.getId(), fileId))) {
            throw new BadRequestException("File with id " + fileId + " not exists in order " + orderId);
        }
        fileService.deleteFile(fileId);
        return ResponseEntity.ok("File deleted successfully.");
    }
}