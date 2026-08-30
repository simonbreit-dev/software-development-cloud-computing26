package de.quadflal.sdfccbackend.list;

import de.quadflal.sdfccbackend.adapter.out.persistence.ListPersistenceAdapter;
import de.quadflal.sdfccbackend.adapter.out.persistence.RestaurantEntity;
import de.quadflal.sdfccbackend.adapter.out.persistence.RestaurantListEntity;
import de.quadflal.sdfccbackend.adapter.out.persistence.RestaurantListRepository;
import de.quadflal.sdfccbackend.adapter.out.persistence.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ListPersistenceAdapterTest {

    @Autowired
    private RestaurantListRepository restaurantListRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ListPersistenceAdapter listPersistenceAdapter;

    @Test
    void addRestaurantToList_persistsAssociationAndUpdatesCount() {
        UUID listId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();

        restaurantListRepository.save(new RestaurantListEntity(
                listId,
                "Top Dinner",
                "Favorites",
                UUID.randomUUID(),
                0,
                OffsetDateTime.now(),
                null
        ));

        restaurantRepository.save(new RestaurantEntity(
                restaurantId,
                "Aroma Bistro",
                "Nice place",
                "Test Street 1",
                "Berlin",
                "10115",
                "Germany",
                52.52,
                13.40,
                OffsetDateTime.now(),
                null
        ));

        listPersistenceAdapter.addRestaurantToList(listId, restaurantId);

        assertThat(listPersistenceAdapter.restaurantExistsInList(listId, restaurantId)).isTrue();
        assertThat(listPersistenceAdapter.findById(listId)).isPresent();
        assertThat(listPersistenceAdapter.findById(listId).orElseThrow().restaurantCount()).isEqualTo(1);
    }
}
