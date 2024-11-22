package pumrapee.repairmanagementapi.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pumrapee.repairmanagementapi.dtos.OrderRequestDTO;
import pumrapee.repairmanagementapi.dtos.OrderResponseDTO;
import pumrapee.repairmanagementapi.entities.Order;
import pumrapee.repairmanagementapi.exceptions.ItemNotFoundException;
import pumrapee.repairmanagementapi.jwts.JwtUserDetailsService;
import pumrapee.repairmanagementapi.repositories.FileRepository;
import pumrapee.repairmanagementapi.repositories.OrderRepository;
import pumrapee.repairmanagementapi.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<OrderResponseDTO> getOrders(String search) {
        List<Order> orders;
        if (search != null && !search.isBlank()) {
            orders = orderRepository.search(search);
        } else {
            orders =  orderRepository.findAll();
        }
        return orders.stream().map(order -> modelMapper.map(order, OrderResponseDTO.class)).collect(Collectors.toList());
    }

    public Order getOrder(Integer id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public OrderResponseDTO saveOrder(OrderRequestDTO newOrder) {
        Order order = modelMapper.map(newOrder, Order.class);
        order.setCreatedBy(userRepository.findById(jwtUserDetailsService.getCurrentUser().getUserId()).orElseThrow(() -> new ItemNotFoundException("User not found")));
        return modelMapper.map(orderRepository.save(order), OrderResponseDTO.class);
    }

    public OrderResponseDTO updateOrder(Integer id, OrderRequestDTO order) {
        Order existingOrder = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        existingOrder.setName(order.getName());
        existingOrder.setStatus(order.getStatus());
        existingOrder.setDescription(order.getDescription());
        return modelMapper.map(orderRepository.save(existingOrder), OrderResponseDTO.class);
    }

    public void deleteOrder(Integer id) {
        Order existingOrder = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        orderRepository.delete(existingOrder);
    }
}
