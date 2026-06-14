package de.quadflal.sdfccbackend.core.model;

public record RestaurantFilter(
        String search,
        String city,
        String country
) {}
