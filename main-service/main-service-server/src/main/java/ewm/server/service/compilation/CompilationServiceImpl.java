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
        Compilation toBeAdded = CompilationMapper.mapDtoToModel(newCompilationDto);
        if (newCompilationDto.getEvents() != null) {
            List<Event> eventsToBeCompiled = eventRepo.findAllById(newCompilationDto.getEvents());
            toBeAdded.setEvents(new HashSet<>(eventsToBeCompiled));
        } else {
            toBeAdded.setEvents(new HashSet<>());
        }
        return CompilationMapper.mapModelToDto(compilationRepo.save(toBeAdded), statsClient);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request) {
        Compilation toBeUpdated = compilationRepo.findById(compId).orElseThrow(() -> {
            throw new CompilationNotFoundException(String.format("Compilation %d does not exist", compId));
        });
        updateEvents(toBeUpdated, request);
        updatePinned(toBeUpdated, request);
        updateTitle(toBeUpdated, request);
        return CompilationMapper.mapModelToDto(compilationRepo.save(toBeUpdated), statsClient);
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilationFound = compilationRepo.findById(compId).orElseThrow(() -> {
            throw new CompilationNotFoundException(String.format("Compilation %d does not exist", compId));
        });
        return CompilationMapper.mapModelToDto(compilationFound, statsClient);
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
        return compilations.stream().map(c -> CompilationMapper.mapModelToDto(c, statsClient))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        compilationRepo.deleteById(compId);
    }

    private Pageable makePageRequest(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

    private void updateTitle(Compilation toBeUpdated, UpdateCompilationRequest request) {
        if (request.getTitle() != null) {
            toBeUpdated.setTitle(request.getTitle());
        }
    }

    private void updatePinned(Compilation toBeUpdated, UpdateCompilationRequest request) {
        if (request.getPinned() != null) {
            toBeUpdated.setPinned(request.getPinned());
        }
    }

    private void updateEvents(Compilation toBeUpdated, UpdateCompilationRequest request) {
        if (request.getEvents() != null) {
            List<Event> eventsToBeUpdated = eventRepo.findAllById(request.getEvents());
            toBeUpdated.setEvents(new HashSet<>(eventsToBeUpdated));
        }
    }
}