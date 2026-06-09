package de.quadflal.sdfccbackend.core;

import de.quadflal.sdfccbackend.adapter.in.web.generated.model.LoginRequest;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.LoginResponse;
import de.quadflal.sdfccbackend.port.in.web.AuthPort;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthPort {
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        return new LoginResponse("blabla", LoginResponse.TokenTypeEnum.BEARER, 3600);
    }
}
