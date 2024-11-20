package pumrapee.repairmanagementapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pumrapee.repairmanagementapi.entities.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o FROM Order o WHERE o.name LIKE %:search% OR o.status LIKE %:search%")
    List<Order> search(@Param("search") String search);
}
