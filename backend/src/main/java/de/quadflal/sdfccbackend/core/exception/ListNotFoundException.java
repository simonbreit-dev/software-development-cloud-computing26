package de.quadflal.sdfccbackend.core.exception;

public class ListNotFoundException extends RuntimeException {

    public ListNotFoundException() {
        super("List not found");
    }
}
