package pumrapee.repairmanagementapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pumrapee.repairmanagementapi.entities.File;
import pumrapee.repairmanagementapi.entities.Order;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Integer> {
    List<File> findByOrder(Order order);
}
