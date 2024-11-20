package pumrapee.repairmanagementapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pumrapee.repairmanagementapi.entities.File;
import pumrapee.repairmanagementapi.services.FileService;

import java.util.List;

@RestController
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
    public ResponseEntity<File> getFile(@PathVariable Integer orderId) {
        return ResponseEntity.ok(fileService.getFile(orderId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Integer id) {
        fileService.deleteFile(id);
        return ResponseEntity.ok("File deleted successfully.");
    }
}