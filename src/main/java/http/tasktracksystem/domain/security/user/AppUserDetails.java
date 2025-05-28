package http.tasktracksystem.domain.security.user;

import http.tasktracksystem.domain.dtos.responses.UserSecurityDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class AppUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private Collection<GrantedAuthority> authorities;

    public static AppUserDetails buildUserDetails(UserSecurityDto user) {
        List<GrantedAuthority> userAuthorities = user.roles()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new AppUserDetails(
                user.id(),
                user.username(),
                user.email(),
                user.password(),
                userAuthorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
