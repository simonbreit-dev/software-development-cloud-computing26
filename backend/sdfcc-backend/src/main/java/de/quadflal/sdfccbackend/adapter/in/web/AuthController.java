package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.AuthApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.LoginRequest;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.LoginResponse;
import de.quadflal.sdfccbackend.core.AuthService;
import de.quadflal.sdfccbackend.port.in.web.AuthPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

    //private AuthPort authPort;
    private AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.login(loginRequest), HttpStatus.OK);
    }
}
