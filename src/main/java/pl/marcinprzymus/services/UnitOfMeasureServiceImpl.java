package pl.marcinprzymus.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.marcinprzymus.commands.UnitOfMeasureCommand;
import pl.marcinprzymus.converters.UnitOfMeasureToUnitOfMeasureCommand;
import pl.marcinprzymus.repositories.reactive.UnitOfMeasureReactiveRepository;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {

    private final UnitOfMeasureReactiveRepository unitOfMeasureRepository;
    private final UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand;

    @Override
    public Flux<UnitOfMeasureCommand> listAllUoms() {
        return unitOfMeasureRepository.findAll().map(unitOfMeasureToUnitOfMeasureCommand::convert);
    }
}
