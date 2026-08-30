package de.quadflal.sdfccbackend.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "restaurant_list_restaurants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"list_id", "restaurant_id"})
)
public class RestaurantListRestaurantEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "list_id", nullable = false)
    private RestaurantListEntity restaurantList;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    public RestaurantListRestaurantEntity() {
        this.id = UUID.randomUUID();
    }

    public RestaurantListRestaurantEntity(UUID id, RestaurantListEntity restaurantList, RestaurantEntity restaurant, OffsetDateTime createdAt) {
        this.id = id != null ? id : UUID.randomUUID();
        this.restaurantList = restaurantList;
        this.restaurant = restaurant;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public RestaurantListEntity getRestaurantList() { return restaurantList; }
    public void setRestaurantList(RestaurantListEntity restaurantList) { this.restaurantList = restaurantList; }

    public RestaurantEntity getRestaurant() { return restaurant; }
    public void setRestaurant(RestaurantEntity restaurant) { this.restaurant = restaurant; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
