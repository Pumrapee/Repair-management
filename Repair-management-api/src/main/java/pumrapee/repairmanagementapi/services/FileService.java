package pumrapee.repairmanagementapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pumrapee.repairmanagementapi.entities.File;
import pumrapee.repairmanagementapi.entities.Order;
import pumrapee.repairmanagementapi.exceptions.IOExceptionHandler;
import pumrapee.repairmanagementapi.exceptions.ItemNotFoundException;
import pumrapee.repairmanagementapi.repositories.FileRepository;
import pumrapee.repairmanagementapi.repositories.OrderRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;

    private final String UPLOAD_DIR = "uploads/";
    @Autowired
    private OrderRepository orderRepository;

    public String saveFile(Integer orderId, MultipartFile file) {
        try {
            java.io.File uploadDir = new java.io.File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String filePath = UPLOAD_DIR + file.getOriginalFilename();
            Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            Order order = orderRepository.findById(orderId).orElseThrow(() -> new ItemNotFoundException("Order not found"));
            File fileEntity = new File();
            fileEntity.setFileName(file.getOriginalFilename());
            fileEntity.setFilePath(filePath);
            fileEntity.setOrder(order);
            fileRepository.save(fileEntity);

            return filePath;
        } catch (IOException e) {
            throw new IOExceptionHandler("Failed to upload file to file system.");
        }
    }

    public List<File> getFileByOrder(Integer orderId) {
        List<File> file = fileRepository.findByOrder(orderRepository.findById(orderId).orElseThrow(() -> new ItemNotFoundException("Order not found")));
        if (file == null) {
            throw new ItemNotFoundException("File not found");
        }
        return file;
    }

    public File getFileById(Integer fileId) {
        File file = fileRepository.findById(fileId).orElseThrow(() -> new ItemNotFoundException("File not found"));
        return file;
    }

    public void deleteFile(Integer fileId) {
        File file = fileRepository.findById(fileId).orElseThrow(() -> new ItemNotFoundException("File not found"));
        Path filePath = Paths.get(file.getFilePath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new IOExceptionHandler("Failed to delete file from file system.");
        }
        fileRepository.delete(file);
    }
}
