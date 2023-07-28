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
    public CompilationDto createNewCompilation(NewCompilationDto newCompilationDto) {

        Compilation compilation = CompilationMapper.mapDtoToModel(newCompilationDto);

        if (newCompilationDto.getEvents() != null) {
            List<Event> events = eventRepo.findAllById(newCompilationDto.getEvents());
            compilation.setEvents(new HashSet<>(events));
        } else {
            compilation.setEvents(new HashSet<>());
        }

        return CompilationMapper.mapModelToDto(compilationRepo.save(compilation), statsClient);
    }

    @Transactional
    @Override
    public List<CompilationDto> getAllCompilations(Optional<Boolean> pinned, Pageable pageable) {

        List<Compilation> compilations;

        if (pinned.isEmpty()) {
            compilations = compilationRepo.findAll(pageable).getContent();
        } else {
            compilations = compilationRepo.findAllByPinned(pinned.get());
        }

        return compilations
                .stream()
                .map(compilation -> CompilationMapper.mapModelToDto(compilation, statsClient))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compilationId) {

        Compilation compilation = compilationRepo.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException(String.format("Compilation %d does not exist", compilationId)));

        return CompilationMapper.mapModelToDto(compilation, statsClient);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilationById(Long compilationId, UpdateCompilationRequest updateCompilationRequest) {

        Compilation compilation = compilationRepo.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException(String.format("Compilation %d does not exist", compilationId)));
        updateEvents(compilation, updateCompilationRequest);
        updatePinned(compilation, updateCompilationRequest);
        updateTitle(compilation, updateCompilationRequest);

        return CompilationMapper.mapModelToDto(compilationRepo.save(compilation), statsClient);
    }

    @Override
    public void deleteCompilationById(Long compilationId) {

        compilationRepo.deleteById(compilationId);
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
            List<Event> events = eventRepo.findAllById(updateCompilationRequest.getEvents());
            compilation.setEvents(new HashSet<>(events));
        }
    }
}