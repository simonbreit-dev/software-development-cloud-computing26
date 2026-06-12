package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.UsersApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.UserResponse;
import de.quadflal.sdfccbackend.port.in.web.UserPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController implements UsersApi {

    private UserPort userPort;

    public UsersController(UserPort userPort) {
        this.userPort = userPort;
    }

    @Override
    public ResponseEntity<UserResponse> getCurrentUser() {
        return null;
    }
}
