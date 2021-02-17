package pl.marcinprzymus.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.marcinprzymus.domain.Recipe;
import pl.marcinprzymus.services.RecipeService;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class WebConfigTest {

    @Mock
    private RecipeService recipeService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        var webConfig = new WebConfig();
        var routerFunction = webConfig.routes(recipeService);
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void testGetRecipes() {

        when(recipeService.getRecipes()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/recipes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetRecipesWithData() {
        when(recipeService.getRecipes()).thenReturn(Flux.just(new Recipe(), new Recipe()));

        webTestClient.get()
                .uri("/api/recipes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Recipe.class);
    }

}