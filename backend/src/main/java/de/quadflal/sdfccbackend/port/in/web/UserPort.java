package de.quadflal.sdfccbackend.port.in.web;

import de.quadflal.sdfccbackend.core.model.User;

public interface UserPort {

    User getCurrentUser(String usernameOrEmail);
}
