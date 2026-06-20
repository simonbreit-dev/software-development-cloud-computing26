package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.UsersApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.UserResponse;
import de.quadflal.sdfccbackend.core.model.User;
import de.quadflal.sdfccbackend.port.in.web.UserPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController implements UsersApi {

    private final UserPort userPort;

    public UsersController(UserPort userPort) {
        this.userPort = userPort;
    }

    @Override
    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userPort.getCurrentUser(authentication.getName());
        UserResponse response = new UserResponse(user.id(), user.username(), user.email(), user.createdAt());
        return ResponseEntity.ok(response);
    }
}
