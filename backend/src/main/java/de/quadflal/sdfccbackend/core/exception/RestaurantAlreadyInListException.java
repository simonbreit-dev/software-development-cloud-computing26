package de.quadflal.sdfccbackend.core.exception;

public class RestaurantAlreadyInListException extends RuntimeException {

    public RestaurantAlreadyInListException() {
        super("Restaurant already in list");
    }
}
