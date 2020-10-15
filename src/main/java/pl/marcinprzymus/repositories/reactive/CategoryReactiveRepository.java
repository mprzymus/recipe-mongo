package pl.marcinprzymus.repositories.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pl.marcinprzymus.domain.Category;

public interface CategoryReactiveRepository extends ReactiveMongoRepository<Category, String> {
}
