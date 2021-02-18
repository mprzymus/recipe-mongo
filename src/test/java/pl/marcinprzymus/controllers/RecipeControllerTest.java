package pl.marcinprzymus.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.marcinprzymus.commands.RecipeCommand;
import pl.marcinprzymus.domain.Notes;
import pl.marcinprzymus.domain.Recipe;
import pl.marcinprzymus.exceptions.NotFoundException;
import pl.marcinprzymus.services.RecipeService;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(RecipeController.class)
@Import(ThymeleafAutoConfiguration.class)
public class RecipeControllerTest {

    @MockBean
    RecipeService recipeService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testGetRecipe() {

        Recipe recipe = new Recipe();
        recipe.setId("1");
        recipe.setNotes(new Notes());

        when(recipeService.findById(anyString())).thenReturn(Mono.just(recipe));

        webTestClient.get().uri("/recipe/1/show").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> assertFalse(response.getResponseBody().isEmpty()));
    }

    @Test
    public void testGetRecipeNotFound() {

        when(recipeService.findById(anyString())).thenThrow(NotFoundException.class);

        webTestClient.get().uri("/recipe/1/show").exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testGetNewRecipeForm() {
        RecipeCommand command = new RecipeCommand();

        webTestClient.get().uri("/recipe/new").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> assertFalse(response.getResponseBody().isEmpty()));
    }

    @Test
    public void testPostNewRecipeForm() {
        RecipeCommand command = new RecipeCommand();
        command.setId("2");

        when(recipeService.saveRecipeCommand(any())).thenReturn(Mono.just(command));

        webTestClient.post().uri("/recipe").exchange()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> assertFalse(response.getResponseBody().isEmpty()));
    }

    @Test
    public void testPostNewRecipeFormValidationFail() {
        RecipeCommand command = new RecipeCommand();
        command.setId("2");

        when(recipeService.saveRecipeCommand(any())).thenReturn(Mono.just(command));

        webTestClient.post().uri("/recipe").exchange()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> assertFalse(response.getResponseBody().isEmpty()));
    }

    @Test
    public void testGetUpdateView() {
        RecipeCommand command = new RecipeCommand();
        command.setId("2");

        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(command));

        webTestClient.get().uri("/recipe/1/update").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> assertFalse(response.getResponseBody().isEmpty()));
    }

    @Test
    public void testDeleteAction() {
        when(recipeService.deleteById(anyString())).thenReturn(Mono.empty());

        webTestClient.get().uri("/recipe/1/delete").exchange()
                .expectStatus().is3xxRedirection();

        verify(recipeService, times(1)).deleteById(anyString());
    }
}