package pl.marcinprzymus.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.marcinprzymus.commands.IngredientCommand;
import pl.marcinprzymus.converters.IngredientCommandToIngredient;
import pl.marcinprzymus.converters.IngredientToIngredientCommand;
import pl.marcinprzymus.domain.Ingredient;
import pl.marcinprzymus.domain.Recipe;
import pl.marcinprzymus.repositories.RecipeRepository;
import pl.marcinprzymus.repositories.reactive.RecipeReactiveRepository;
import pl.marcinprzymus.repositories.reactive.UnitOfMeasureReactiveRepository;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final RecipeReactiveRepository recipeReactiveRepository;
    private final UnitOfMeasureReactiveRepository unitOfMeasureRepository;

    @Override
    public Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId) {

        return recipeReactiveRepository
                .findById(recipeId)
                .flatMapIterable(Recipe::getIngredients)
                .filter(ingredient -> ingredient.getId().equalsIgnoreCase(ingredientId))
                .single()
                .map(ingredient -> {
                    var command = ingredientToIngredientCommand.convert(ingredient);
                    assert command != null;
                    command.setRecipeId(recipeId);
                    return command;
                });
    }

    @Override
    public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command) {
        AtomicReference<String> ingredientId = new AtomicReference<>();
        AtomicReference<String> recipeId = new AtomicReference<>();
        return recipeReactiveRepository.findById(command.getRecipeId())
                .map(recipe -> {
                    recipeId.set(recipe.getId());
                    recipe.getIngredients().stream()
                            .filter(ingredient -> ingredient.getId().equalsIgnoreCase(command.getId()))
                            .findFirst()
                            .map(ingredient -> {
                                ingredientId.set(command.getId());
                                ingredient.setDescription(command.getDescription());
                                ingredient.setAmount(command.getAmount());
                                return recipe;
                            }).orElseGet(() -> {
                        Ingredient newIngredient = ingredientCommandToIngredient.convert(command);
                        ingredientId.set(Objects.requireNonNull(newIngredient).getId());
                        unitOfMeasureRepository.findById(command.getUom()
                                .getId())
                                .flatMap(unitOfMeasure -> {
                                    newIngredient.setUom(unitOfMeasure);
                                    return Mono.just(unitOfMeasure);
                                }).subscribe();
                        recipe.addIngredient(newIngredient);
                        return recipe;
                    });
                    return recipe;
                })
                .flatMap(recipe -> recipeReactiveRepository.save(recipe).or(Mono.just(recipe)))
                .flatMapIterable(Recipe::getIngredients)
                .filter(savedIngredient -> savedIngredient.getId().equalsIgnoreCase(ingredientId.get()))
                .flatMap(savedIngredient -> {
                    IngredientCommand ingredientCommand = ingredientToIngredientCommand.convert(savedIngredient);
                    ingredientCommand.setRecipeId(recipeId.get());
                    return Mono.justOrEmpty(ingredientCommand);
                }).single();
    }


    @Override
    public Mono<Void> deleteById(String recipeId, String idToDelete) {

        log.debug("Deleting ingredient: " + recipeId + ":" + idToDelete);

        Recipe recipe = recipeReactiveRepository.findById(recipeId).block();

        if (recipe != null) {
            log.debug("found recipe");

            Optional<Ingredient> ingredientOptional = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(idToDelete))
                    .findFirst();

            if (ingredientOptional.isPresent()) {
                log.debug("found Ingredient");

                recipe.getIngredients().remove(ingredientOptional.get());
                recipeReactiveRepository.save(recipe).block();
            }
        } else {
            log.debug("Recipe Id Not found. Id:" + recipeId);
        }
        return Mono.empty();
    }
}
