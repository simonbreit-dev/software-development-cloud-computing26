package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.AuthApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.LoginRequest;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.LoginResponse;
import de.quadflal.sdfccbackend.port.in.web.AuthPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

    private AuthPort authPort;


    public AuthController(AuthPort authPort) {
        this.authPort = authPort;
    }

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        return new ResponseEntity<>(new LoginResponse("", null, 1), HttpStatus.OK);
    }
}
