package pl.marcinprzymus.controllers;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.marcinprzymus.commands.IngredientCommand;
import pl.marcinprzymus.commands.RecipeCommand;
import pl.marcinprzymus.commands.UnitOfMeasureCommand;
import pl.marcinprzymus.services.IngredientService;
import pl.marcinprzymus.services.RecipeService;
import pl.marcinprzymus.services.UnitOfMeasureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(IngredientController.class)
@Import(ThymeleafAutoConfiguration.class)
public class IngredientControllerTest {

    @MockBean
    IngredientService ingredientService;

    @MockBean
    UnitOfMeasureService unitOfMeasureService;

    @MockBean
    RecipeService recipeService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testListIngredients() {
        //given
        RecipeCommand recipeCommand = new RecipeCommand();
        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(recipeCommand));

        //when
        webTestClient.get().uri("/recipe/1/ingredients").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> assertFalse(response.getResponseBody().isEmpty()));

        //then
        verify(recipeService, times(1)).findCommandById(anyString());
    }

    @Test
    public void testShowIngredient() {
        //given
        var ingredient = new IngredientCommand();
        ingredient.setUom(new UnitOfMeasureCommand());
        Mono<IngredientCommand> ingredientCommandMono = Mono.just(ingredient);

        //when
        when(ingredientService.findByRecipeIdAndIngredientId(anyString(), anyString())).thenReturn(ingredientCommandMono);

        //then
        webTestClient.get().uri("/recipe/1/ingredient/2/show").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> assertFalse(response.getResponseBody().isEmpty()));
        ;
    }

    @Test
    public void testNewIngredientForm() {
        //given
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1");
        var uom = new UnitOfMeasureCommand();
        uom.setId("1");
        uom.setDescription("desc");
        //when
        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(recipeCommand));
        when(unitOfMeasureService.listAllUoms()).thenReturn(Flux.just(uom));

        //then
        webTestClient.get().uri("/recipe/1/ingredient/new").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> assertFalse(response.getResponseBody().isEmpty()));

        verify(recipeService, times(1)).findCommandById(anyString());

    }

    @Test
    public void testUpdateIngredientForm() {
        //given
        final String id = "1";
        var ingredientCommand = new IngredientCommand();
        ingredientCommand.setId(id);
        var uom = new UnitOfMeasureCommand();
        uom.setId(id);
        ingredientCommand.setUom(uom);

        //when
        when(ingredientService.findByRecipeIdAndIngredientId(anyString(), anyString())).thenReturn(Mono.just(ingredientCommand));
        when(unitOfMeasureService.listAllUoms()).thenReturn(Flux.just(uom));

        //then
        webTestClient.get().uri("/recipe/1/ingredient/2/update").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> assertFalse(response.getResponseBody().isEmpty()));
    }

    @Test
    public void testSaveOrUpdate() {
        //given
        IngredientCommand command = new IngredientCommand();
        command.setId("3");
        command.setRecipeId("2");
        Mono<IngredientCommand> mono = Mono.just(command);

        //when
        when(ingredientService.saveIngredientCommand(any())).thenReturn(mono);

        //then
        webTestClient.post().uri("/recipe/2/ingredient").exchange()
                .expectStatus().isOk();

    }

    @Test
    public void testDeleteIngredient() throws Exception {
        when(ingredientService.deleteById(anyString(), anyString())).thenReturn(Mono.empty());

        //then
        webTestClient.get().uri("/recipe/2/ingredient/3/delete").exchange()
                .expectStatus().is3xxRedirection();
        verify(ingredientService, times(1)).deleteById(anyString(), anyString());

    }
}