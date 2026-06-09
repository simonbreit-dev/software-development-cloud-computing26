package de.quadflal.sdfccbackend.port.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.model.LoginRequest;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.LoginResponse;

public interface AuthPort {

    LoginResponse login(LoginRequest loginRequest);


}
