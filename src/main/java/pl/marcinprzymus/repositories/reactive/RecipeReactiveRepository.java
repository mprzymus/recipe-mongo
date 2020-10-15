package pl.marcinprzymus.repositories.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pl.marcinprzymus.domain.Recipe;

public interface RecipeReactiveRepository  extends ReactiveMongoRepository<Recipe, String> {
}
