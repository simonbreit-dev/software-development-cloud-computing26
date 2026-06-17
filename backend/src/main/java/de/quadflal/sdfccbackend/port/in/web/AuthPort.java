package de.quadflal.sdfccbackend.port.in.web;

import de.quadflal.sdfccbackend.core.model.LoginCommand;
import de.quadflal.sdfccbackend.core.model.LoginResult;

public interface AuthPort {

    LoginResult login(LoginCommand command);
}
