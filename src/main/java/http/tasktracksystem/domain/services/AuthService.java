package http.tasktracksystem.domain.services;

import http.tasktracksystem.domain.dtos.responses.TokenResponse;
import http.tasktracksystem.domain.security.jwt.JwtUtils;
import http.tasktracksystem.domain.security.user.AppUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    /**
     * @param username Username to be loggedIn.
     * @param password Password of the user.
     * @return TokenResponse - Id,  Username, Email and Token.
     */
    public TokenResponse login(String username,
                               String password) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        AppUserDetails principal = (AppUserDetails) authentication.getPrincipal();

        return TokenResponse.builder()
                .id(principal.getId())
                .username(principal.getUsername())
                .email(principal.getEmail())
                .token(jwtUtils.generateTokenForUser(authentication))
                .build();
    }
}
