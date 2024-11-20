package pumrapee.repairmanagementapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pumrapee.repairmanagementapi.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
}
