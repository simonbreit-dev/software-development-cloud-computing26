package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.UsersApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.UserResponse;
import de.quadflal.sdfccbackend.core.model.User;
import de.quadflal.sdfccbackend.port.in.web.UserPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userPort.findByUsername(username)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));

        UserResponse response = new UserResponse();
        response.setId(user.id());
        response.setUsername(user.username());
        response.setEmail(user.email());
        response.setCreatedAt(user.createdAt());
        return ResponseEntity.ok(response);
    }
}
