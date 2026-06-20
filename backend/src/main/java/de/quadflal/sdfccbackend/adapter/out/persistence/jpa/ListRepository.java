package de.quadflal.sdfccbackend.adapter.out.persistence.jpa;

import de.quadflal.sdfccbackend.adapter.out.persistence.model.ListPersistenceModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ListRepository extends JpaRepository<ListPersistenceModel, UUID> {
}
