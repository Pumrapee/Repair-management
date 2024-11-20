package pumrapee.repairmanagementapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pumrapee.repairmanagementapi.entities.File;
import pumrapee.repairmanagementapi.entities.Order;

public interface FileRepository extends JpaRepository<File, Integer> {
    File findByOrder(Order order);
}
