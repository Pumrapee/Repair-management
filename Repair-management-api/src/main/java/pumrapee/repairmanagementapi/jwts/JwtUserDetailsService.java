package pumrapee.repairmanagementapi.jwts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pumrapee.repairmanagementapi.entities.AuthUser;
import pumrapee.repairmanagementapi.entities.User;
import pumrapee.repairmanagementapi.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) {
        User user = userRepository.findByUsername(userName);
        if (user == null) { throw new UsernameNotFoundException(userName); }
        List<GrantedAuthority> roles = new ArrayList<>();
        GrantedAuthority grantedAuthority = () -> user.getRole().toString();
        roles.add(grantedAuthority);
        return new AuthUser(userName, user.getPassword(), roles, user.getId());
    }

    public AuthUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return (AuthUser) authentication.getPrincipal();
        }

        return null;
    }

}
