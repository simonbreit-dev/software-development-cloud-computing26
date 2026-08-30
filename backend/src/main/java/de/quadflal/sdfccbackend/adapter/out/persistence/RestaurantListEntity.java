package de.quadflal.sdfccbackend.adapter.out.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "restaurant_lists")
public class RestaurantListEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private UUID ownerId;

    @Column(nullable = false)
    private Integer restaurantCount;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "restaurantList", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RestaurantListRestaurantEntity> restaurantAssociations = new HashSet<>();

    public RestaurantListEntity() {
    }

    public RestaurantListEntity(UUID id, String name, String description, UUID ownerId,
                               Integer restaurantCount, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.restaurantCount = restaurantCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }

    public Integer getRestaurantCount() { return restaurantCount; }
    public void setRestaurantCount(Integer restaurantCount) { this.restaurantCount = restaurantCount; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Set<RestaurantListRestaurantEntity> getRestaurantAssociations() { return restaurantAssociations; }
    public void setRestaurantAssociations(Set<RestaurantListRestaurantEntity> restaurantAssociations) { this.restaurantAssociations = restaurantAssociations; }
}
