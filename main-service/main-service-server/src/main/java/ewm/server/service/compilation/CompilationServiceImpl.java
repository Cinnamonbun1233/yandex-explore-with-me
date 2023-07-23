package ewm.server.service.compilation;

import ewm.client.StatsClient;
import ewm.server.dto.compilation.CompilationDto;
import ewm.server.dto.compilation.NewCompilationDto;
import ewm.server.dto.compilation.UpdateCompilationRequest;
import ewm.server.exception.compilation.CompilationNotFoundException;
import ewm.server.mapper.compilation.CompilationMapper;
import ewm.server.model.compilation.Compilation;
import ewm.server.model.event.Event;
import ewm.server.repo.compilation.CompilationRepo;
import ewm.server.repo.event.EventRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepo compilationRepo;
    private final EventRepo eventRepo;
    private final StatsClient statsClient;

    @Transactional
    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.mapDtoToModel(newCompilationDto);

        if (newCompilationDto.getEvents() != null) {
            List<Event> eventsToBeCompiled = eventRepo.findAllById(newCompilationDto.getEvents());
            compilation.setEvents(new HashSet<>(eventsToBeCompiled));
        } else {
            compilation.setEvents(new HashSet<>());
        }

        return CompilationMapper.mapModelToDto(compilationRepo.save(compilation), statsClient);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Optional<Boolean> pinned, int from, int size) {
        Pageable request = makePageRequest(from, size);
        List<Compilation> compilations;

        if (pinned.isEmpty()) {
            compilations = compilationRepo.findAll(request).getContent();
        } else {
            compilations = compilationRepo.findAllByPinned(pinned.get());
        }

        return compilations
                .stream()
                .map(c -> CompilationMapper.mapModelToDto(c, statsClient))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepo.findById(compId).orElseThrow(
                () -> new CompilationNotFoundException(String.format("Compilation %d does not exist", compId)));
        return CompilationMapper.mapModelToDto(compilation, statsClient);
    }


    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation toBeUpdated = compilationRepo.findById(compId).orElseThrow(
                () -> new CompilationNotFoundException(String.format("Compilation %d does not exist", compId)));
        updateEvents(toBeUpdated, updateCompilationRequest);
        updatePinned(toBeUpdated, updateCompilationRequest);
        updateTitle(toBeUpdated, updateCompilationRequest);
        return CompilationMapper.mapModelToDto(compilationRepo.save(toBeUpdated), statsClient);
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        compilationRepo.deleteById(compId);
    }

    private Pageable makePageRequest(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

    private void updateTitle(Compilation compilation, UpdateCompilationRequest updateCompilationRequest) {
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
    }

    private void updatePinned(Compilation compilation, UpdateCompilationRequest updateCompilationRequest) {
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
    }

    private void updateEvents(Compilation compilation, UpdateCompilationRequest updateCompilationRequest) {
        if (updateCompilationRequest.getEvents() != null) {
            List<Event> eventsToBeUpdated = eventRepo.findAllById(updateCompilationRequest.getEvents());
            compilation.setEvents(new HashSet<>(eventsToBeUpdated));
        }
    }
}