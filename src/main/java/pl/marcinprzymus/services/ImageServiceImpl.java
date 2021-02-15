package pl.marcinprzymus.services;

import lombok.RequiredArgsConstructor;
import pl.marcinprzymus.domain.Recipe;
import pl.marcinprzymus.repositories.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.marcinprzymus.repositories.reactive.RecipeReactiveRepository;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final RecipeReactiveRepository recipeRepository;

    @Override
    public Mono<Void> saveImageFile(String recipeId, MultipartFile file) {
        var recipe = recipeRepository.findById(recipeId);
        recipe = recipe.map(rc -> {
            try {
                Byte[] byteObjects = new Byte[file.getBytes().length];
                int i = 0;
                for (byte b : file.getBytes()) {
                    byteObjects[i++] = b;
                }
                rc.setImage(byteObjects);
                return rc;
            } catch (IOException e) {
                log.error("Error occurred", e);
                e.printStackTrace();
                throw new RuntimeException();
            }
        });
        recipeRepository.save(recipe.block()).block();
        return Mono.empty();
    }
}
