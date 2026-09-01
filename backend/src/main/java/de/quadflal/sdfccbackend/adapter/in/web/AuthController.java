package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.AuthApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.LoginRequest;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.LoginResponse;
import de.quadflal.sdfccbackend.core.model.User;
import de.quadflal.sdfccbackend.port.in.web.AuthPort;
import de.quadflal.sdfccbackend.security.JwtTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AuthController implements AuthApi {

    private final AuthPort authPort;
    private final JwtTokenService jwtTokenService;

    public AuthController(AuthPort authPort, JwtTokenService jwtTokenService) {
        this.authPort = authPort;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        Optional<User> user = authPort.login(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String accessToken = jwtTokenService.generateToken(user.get().username());

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setTokenType(LoginResponse.TokenTypeEnum.BEARER);
        response.setExpiresIn((int) jwtTokenService.getExpirySeconds());
        return ResponseEntity.ok(response);
    }
}
