package pl.marcinprzymus.repositories.reactive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.marcinprzymus.domain.Category;
import pl.marcinprzymus.repositories.reactive.CategoryReactiveRepository;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class CategoryReactiveRepositoryTest {

    @Autowired
    CategoryReactiveRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll().block();
    }

    @Test
    void saveTest() {
        Category category1 = new Category();
        category1.setId("Id1");
        Category category2 = new Category();
        category2.setId("Id2");

        repository.saveAll(Flux.just(category1, category2)).blockLast();

        assertEquals(2,repository.count().block());
    }
}
