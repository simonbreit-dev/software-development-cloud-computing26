package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.UsersApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.UserResponse;
import de.quadflal.sdfccbackend.port.in.web.UserPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
public class UsersController implements UsersApi {

    private final UserPort userPort;

    public UsersController(UserPort userPort) {
        this.userPort = userPort;
    }

    @Override
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse response = new UserResponse();
        response.setId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"));
        response.setUsername("api-user");
        response.setEmail("api-user@example.com");
        response.setCreatedAt(OffsetDateTime.now());
        return ResponseEntity.ok(response);
    }
}
