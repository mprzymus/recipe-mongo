package pl.marcinprzymus.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.marcinprzymus.domain.Recipe;
import pl.marcinprzymus.services.RecipeService;
import reactor.core.publisher.Flux;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(IndexController.class)
@Import(ThymeleafAutoConfiguration.class)
public class IndexControllerTest {

    @MockBean
    private RecipeService recipeService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testWebClient() {

        Recipe recipe = new Recipe();
        recipe.setDescription("Fajita");
        when(recipeService.getRecipes()).thenReturn(Flux.just(recipe));

        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String responseBody = Objects.requireNonNull(response.getResponseBody());
                    assertTrue(responseBody.contains("Fajita"));
                });
    }

    /*@Test
    public void getIndexPage() throws Exception {

        //given
        Recipe recipe = new Recipe();
        recipe.setId("1");

        when(recipeService.getRecipes()).thenReturn(Flux.just(new Recipe(), recipe));

        ArgumentCaptor<List<Recipe>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        //when
        String viewName = controller.getIndexPage(model);


        //then
        assertEquals("index", viewName);
        verify(recipeService, times(1)).getRecipes();
        verify(model, times(1)).addAttribute(eq("recipes"), argumentCaptor.capture());
        var setInController = argumentCaptor.getValue();
        assertEquals(2, setInController.size());
    }*/

}