package pumrapee.repairmanagementapi.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pumrapee.repairmanagementapi.dtos.OrderRequestDTO;
import pumrapee.repairmanagementapi.dtos.OrderResponseDTO;
import pumrapee.repairmanagementapi.entities.File;
import pumrapee.repairmanagementapi.entities.Order;
import pumrapee.repairmanagementapi.services.FileService;
import pumrapee.repairmanagementapi.services.OrderService;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://repair.eastasia.cloudapp.azure.com"})
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private FileService fileService;

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(orderService.getOrders(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO order) {
        return ResponseEntity.ok(orderService.saveOrder(order));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable Integer id, @Valid @RequestBody OrderRequestDTO order) {
        return ResponseEntity.ok(orderService.updateOrder(id, order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Integer id) {
        for (File flie : fileService.getFileByOrder(id)) {
            fileService.deleteFile(flie.getId());
        }
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully.");
    }
}
