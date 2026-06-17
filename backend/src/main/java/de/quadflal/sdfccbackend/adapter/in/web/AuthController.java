package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.AuthApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.LoginRequest;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.LoginResponse;
import de.quadflal.sdfccbackend.core.model.LoginCommand;
import de.quadflal.sdfccbackend.core.model.LoginResult;
import de.quadflal.sdfccbackend.port.in.web.AuthPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

    private final AuthPort authPort;

    public AuthController(AuthPort authPort) {
        this.authPort = authPort;
    }

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        LoginCommand command = new LoginCommand(
                loginRequest.getUsernameOrEmail(),
                loginRequest.getPassword()
        );
        LoginResult result = authPort.login(command);
        LoginResponse response = new LoginResponse(
                result.accessToken(),
                LoginResponse.TokenTypeEnum.BEARER,
                result.expiresIn()
        );
        return ResponseEntity.ok(response);
    }
}
