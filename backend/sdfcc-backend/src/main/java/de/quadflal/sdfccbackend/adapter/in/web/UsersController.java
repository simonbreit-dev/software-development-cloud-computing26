package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.UsersApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController implements UsersApi {
    @Override
    public ResponseEntity<UserResponse> getCurrentUser() {
        return null;
    }
}
