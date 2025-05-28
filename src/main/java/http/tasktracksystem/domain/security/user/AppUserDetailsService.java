package http.tasktracksystem.domain.security.user;

import http.tasktracksystem.domain.dtos.responses.UserSecurityDto;
import http.tasktracksystem.domain.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserSecurityDto userByUsername = this.userService.getUserForSecurity(username);
        return AppUserDetails.buildUserDetails(userByUsername);
    }
}
