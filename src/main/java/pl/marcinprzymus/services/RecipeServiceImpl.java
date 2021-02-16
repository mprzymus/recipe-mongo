package pl.marcinprzymus.services;

import lombok.RequiredArgsConstructor;
import pl.marcinprzymus.commands.RecipeCommand;
import pl.marcinprzymus.converters.RecipeCommandToRecipe;
import pl.marcinprzymus.converters.RecipeToRecipeCommand;
import pl.marcinprzymus.domain.Recipe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.marcinprzymus.repositories.reactive.RecipeReactiveRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeReactiveRepository recipeRepository;
    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;

    @Override
    public Flux<Recipe> getRecipes() {
        log.debug("I'm in the service");

        return recipeRepository.findAll();
    }

    @Override
    public Mono<Recipe> findById(String id) {

        return recipeRepository.findById(id);
    }

    @Override
    public Mono<RecipeCommand> findCommandById(String id) {
        return findById(id)
                .map(recipe -> {
                    var command = recipeToRecipeCommand.convert(recipe);
                    command.getIngredients().forEach(ingredientCommand -> ingredientCommand.setRecipeId(recipe.getId()));
                    return command;
                });
    }

    @Override
    public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
        var detachedRecipe = recipeCommandToRecipe.convert(command);
        var savedRecipe = recipeRepository.save(detachedRecipe);
        return savedRecipe.map(recipeToRecipeCommand::convert);
    }

    @Override
    public Mono<Void> deleteById(String idToDelete) {
        recipeRepository.deleteById(idToDelete);
        return Mono.empty();
    }
}
