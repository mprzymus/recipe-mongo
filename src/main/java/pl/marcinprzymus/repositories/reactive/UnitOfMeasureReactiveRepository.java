package pl.marcinprzymus.repositories.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pl.marcinprzymus.domain.UnitOfMeasure;

import java.util.Optional;

public interface UnitOfMeasureReactiveRepository extends ReactiveMongoRepository<UnitOfMeasure, String> {
    //Optional<UnitOfMeasure> findByDescription(String description);
}
