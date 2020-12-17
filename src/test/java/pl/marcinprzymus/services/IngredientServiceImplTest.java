package pl.marcinprzymus.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.marcinprzymus.commands.IngredientCommand;
import pl.marcinprzymus.converters.IngredientCommandToIngredient;
import pl.marcinprzymus.converters.IngredientToIngredientCommand;
import pl.marcinprzymus.converters.UnitOfMeasureCommandToUnitOfMeasure;
import pl.marcinprzymus.converters.UnitOfMeasureToUnitOfMeasureCommand;
import pl.marcinprzymus.domain.Ingredient;
import pl.marcinprzymus.domain.Recipe;
import pl.marcinprzymus.repositories.RecipeRepository;
import pl.marcinprzymus.repositories.reactive.RecipeReactiveRepository;
import pl.marcinprzymus.repositories.reactive.UnitOfMeasureReactiveRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class IngredientServiceImplTest {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;

    @Mock
    RecipeReactiveRepository recipeRepository;

    @Mock
    UnitOfMeasureReactiveRepository unitOfMeasureRepository;

    @Mock
    RecipeRepository recipeNotReactiveRepository;


    IngredientService ingredientService;

    //init converters
    public IngredientServiceImplTest() {
        this.ingredientToIngredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
        this.ingredientCommandToIngredient = new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure());
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ingredientService = new IngredientServiceImpl(ingredientToIngredientCommand, ingredientCommandToIngredient,
                recipeRepository, recipeNotReactiveRepository, unitOfMeasureRepository);
    }

    @Test
    public void findByRecipeIdAndId() throws Exception {
    }

    @Test
    public void findByRecipeIdAndRecipeIdHappyPath() throws Exception {
        //given
        Recipe recipe = new Recipe();
        recipe.setId("1");

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId("1");

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId("1");

        Ingredient ingredient3 = new Ingredient();
        ingredient3.setId("3");

        recipe.addIngredient(ingredient1);
        recipe.addIngredient(ingredient2);
        recipe.addIngredient(ingredient3);
        var recipeOptional = Mono.just(recipe);

        when(recipeRepository.findById(anyString())).thenReturn(recipeOptional);

        //then
        Mono<IngredientCommand> ingredientCommand = ingredientService.findByRecipeIdAndIngredientId("1", "3");

        //when
        assertEquals("3", ingredientCommand.block().getId());
        assertEquals("1", ingredientCommand.block().getRecipeId());
        verify(recipeRepository, times(1)).findById(anyString());
    }


    @Test
    public void testSaveRecipeCommand() throws Exception {
        //given
        IngredientCommand command = new IngredientCommand();
        command.setId("3");
        command.setRecipeId("2");

        var recipeOptional = Optional.of(new Recipe());

        Recipe savedRecipe = new Recipe();
        savedRecipe.addIngredient(new Ingredient());
        savedRecipe.getIngredients().iterator().next().setId("3");

        when(recipeNotReactiveRepository.findById(anyString())).thenReturn(recipeOptional);
        when(recipeRepository.save(any())).thenReturn(Mono.just(savedRecipe));

        //when
        Mono<IngredientCommand> savedCommand = ingredientService.saveIngredientCommand(command);

        //then
        assertEquals("3", savedCommand.block().getId());
        verify(recipeNotReactiveRepository, times(1)).findById(anyString());
        verify(recipeRepository, times(1)).save(any(Recipe.class));

    }

    @Test
    public void testDeleteById() throws Exception {
        //given
        Recipe recipe = new Recipe();
        recipe.setId("1");
        Ingredient ingredient = new Ingredient();
        ingredient.setId("3");
        recipe.addIngredient(ingredient);
        var recipeOptional = Optional.of(recipe);

        when(recipeNotReactiveRepository.findById(anyString())).thenReturn(recipeOptional);

        //when
        ingredientService.deleteById("1", "3");

        //then
        verify(recipeNotReactiveRepository, times(1)).findById(anyString());
        verify(recipeNotReactiveRepository, times(1)).save(any(Recipe.class));
    }
}