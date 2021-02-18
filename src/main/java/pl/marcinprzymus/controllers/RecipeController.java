package pl.marcinprzymus.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.thymeleaf.exceptions.TemplateInputException;
import pl.marcinprzymus.commands.RecipeCommand;
import pl.marcinprzymus.exceptions.NotFoundException;
import pl.marcinprzymus.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
//import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RecipeController {

    private static final String RECIPE_RECIPEFORM_URL = "recipe/recipeform";
    private final RecipeService recipeService;

    @GetMapping("/recipe/{id}/show")
    public String showById(@PathVariable String id, Model model) {

        model.addAttribute("recipe", recipeService.findById(id));

        return "recipe/show";
    }

    @GetMapping("recipe/new")
    public String newRecipe(Model model) {
        model.addAttribute("recipe", new RecipeCommand());

        return "recipe/recipeform";
    }

    @GetMapping("recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model) {
        model.addAttribute("recipe", recipeService.findCommandById(id));
        return RECIPE_RECIPEFORM_URL;
    }

    @PostMapping("recipe")
    public Mono<String> saveOrUpdate(@Valid @ModelAttribute("recipe") Mono<RecipeCommand> command) {
        return command.flatMap(recipeService::saveRecipeCommand)
                .map(recipe -> {
                    if (recipe.getId() != null && !recipe.getId().isBlank())
                        return "redirect:/recipe/" + recipe.getId() + "/show";
                    else
                        return "redirect:/";
                })
                .doOnError(thr -> log.error("Saving validation error"))
                .onErrorResume(WebExchangeBindException.class, thr -> Mono.just(RECIPE_RECIPEFORM_URL));
    }

    @GetMapping("recipe/{id}/delete")
    public Mono<String> deleteById(@PathVariable String id) {

        log.debug("Deleting id: " + id);

        return recipeService.deleteById(id).thenReturn("redirect:/");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class, TemplateInputException.class})
    public String handleNotFound(Exception exception, Model model) {
        log.error("Handling not found exception");
        log.error(exception.getMessage());

        model.addAttribute("exception", exception);

        return "404error";
    }

}
