package pumrapee.repairmanagementapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pumrapee.repairmanagementapi.dtos.RegisterDTO;
import pumrapee.repairmanagementapi.entities.User;
import pumrapee.repairmanagementapi.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void save(RegisterDTO newUser) {
        String encodedPassword = passwordEncoder.encode(newUser.getPassword());
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    public Boolean exist(String username) {
        return (userRepository.existsByUsername(username));
    }
}
