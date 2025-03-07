package pl.marcinprzymus.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.support.WebExchangeBindException;
import pl.marcinprzymus.commands.IngredientCommand;
import pl.marcinprzymus.commands.RecipeCommand;
import pl.marcinprzymus.commands.UnitOfMeasureCommand;
import pl.marcinprzymus.services.IngredientService;
import pl.marcinprzymus.services.RecipeService;
import pl.marcinprzymus.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class IngredientController {

    public static final String RECIPE_INGREDIENT_INGREDIENTFORM = "recipe/ingredient/ingredientform";
    private final IngredientService ingredientService;
    private final RecipeService recipeService;
    private final UnitOfMeasureService unitOfMeasureService;

    @GetMapping("/recipe/{recipeId}/ingredients")
    public String listIngredients(@PathVariable String recipeId, Model model) {
        log.debug("Getting ingredient list for recipe id: " + recipeId);

        // use command object to avoid lazy load errors in Thymeleaf.
        model.addAttribute("recipe", recipeService.findCommandById(recipeId));

        return "recipe/ingredient/list";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/show")
    public String showRecipeIngredient(@PathVariable String recipeId,
                                       @PathVariable String id, Model model) {
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));
        return "recipe/ingredient/show";
    }

    @GetMapping("recipe/{recipeId}/ingredient/new")
    public String newRecipe(@PathVariable String recipeId, Model model) {

        //make sure we have a good id value
        RecipeCommand recipeCommand = recipeService.findCommandById(recipeId).block();
        //todo raise exception if null

        //need to return back parent id for hidden form property
        IngredientCommand ingredientCommand = new IngredientCommand();
        model.addAttribute("ingredient", ingredientCommand);

        //init uom
        ingredientCommand.setUom(new UnitOfMeasureCommand());

        return "recipe/ingredient/ingredientform";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/update")
    public String updateRecipeIngredient(@PathVariable String recipeId,
                                         @PathVariable String id, Model model) {
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));
        return RECIPE_INGREDIENT_INGREDIENTFORM;
    }

    @PostMapping("recipe/{recipeId}/ingredient")
    public Mono<String> saveOrUpdate(@Valid @ModelAttribute("ingredient") Mono<IngredientCommand> command, Model model) {
        return command
                .flatMap(ingredientService::saveIngredientCommand)
                .map(
                        savedCommand -> "redirect:/recipe/" + savedCommand.getRecipeId()
                                + "/ingredient/" + savedCommand.getId() + "/show"
                )
                .doOnError(thr -> log.error("Saving ingredient validation error"))
                .onErrorResume(WebExchangeBindException.class, thr -> Mono.just(RECIPE_INGREDIENT_INGREDIENTFORM));
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/delete")
    public Mono<String> deleteIngredient(@PathVariable String recipeId, @PathVariable String id) {

        log.debug("deleting ingredient id:" + id);
        return ingredientService.deleteById(recipeId, id)
                .thenReturn("redirect:/recipe/" + recipeId + "/ingredients");
    }

    @ModelAttribute("uomList")
    public Flux<UnitOfMeasureCommand> populateUomList() {
        return unitOfMeasureService.listAllUoms();
    }
}
