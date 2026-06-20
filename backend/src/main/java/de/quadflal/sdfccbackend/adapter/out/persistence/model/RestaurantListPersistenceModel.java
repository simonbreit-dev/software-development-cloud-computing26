package de.quadflal.sdfccbackend.adapter.out.persistence.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "list_restaurants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"list_id", "restaurant_id"})
)
public class RestaurantListPersistenceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "list_id", nullable = false)
    private UUID listId;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getListId() {
        return listId;
    }

    public void setListId(UUID listId) {
        this.listId = listId;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(UUID restaurantId) {
        this.restaurantId = restaurantId;
    }
}
