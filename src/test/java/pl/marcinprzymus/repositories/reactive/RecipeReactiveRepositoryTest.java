package pl.marcinprzymus.repositories.reactive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.marcinprzymus.domain.Recipe;
import pl.marcinprzymus.repositories.reactive.RecipeReactiveRepository;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class RecipeReactiveRepositoryTest {

    @Autowired
    RecipeReactiveRepository repository;


    @BeforeEach
    public void setUp() {
        repository.deleteAll().block();
    }

    @Test
    public void saveTest() {
        Recipe recipe1 = new Recipe();
        recipe1.setId("Id1");
        Recipe recipe2 = new Recipe();
        recipe2.setId("Id2");


        repository.saveAll(Flux.just(recipe1, recipe2)).blockLast();

        assertEquals(2, repository.findAll().count().block());
    }
}