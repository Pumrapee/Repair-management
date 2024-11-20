package pumrapee.repairmanagementapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pumrapee.repairmanagementapi.entities.Order;
import pumrapee.repairmanagementapi.jwts.JwtUserDetailsService;
import pumrapee.repairmanagementapi.repositories.OrderRepository;
import pumrapee.repairmanagementapi.repositories.UserRepository;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private UserRepository userRepository;

    public List<Order> getOrders(String search) {
        if (search != null && !search.isBlank()) {
            return orderRepository.search(search);
        }
        return orderRepository.findAll();
    }

    public Order getOrder(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public Order saveOrder(Order order) {
        userRepository.findById(jwtUserDetailsService.getCurrentUser().getUserId());
        return orderRepository.save(order);
    }

    public Order updateOrder(Integer id, Order order) {
        Order existingOrder = getOrder(id);
        existingOrder.setName(order.getName());
        existingOrder.setStatus(order.getStatus());
        existingOrder.setDescription(order.getDescription());
        return orderRepository.save(existingOrder);
    }

    public void deleteOrder(Integer id) {
        Order existingOrder = getOrder(id);
        orderRepository.delete(existingOrder);
    }
}
