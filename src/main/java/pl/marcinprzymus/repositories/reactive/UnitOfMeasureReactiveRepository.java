package pl.marcinprzymus.repositories.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pl.marcinprzymus.domain.UnitOfMeasure;
import reactor.core.publisher.Mono;

public interface UnitOfMeasureReactiveRepository extends ReactiveMongoRepository<UnitOfMeasure, String> {
    Mono<UnitOfMeasure> findByDescription(String description);
}
