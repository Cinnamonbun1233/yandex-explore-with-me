package ewm.server.service.compilation;

import ewm.server.dto.compilation.CompilationDto;
import ewm.server.dto.compilation.NewCompilationDto;
import ewm.server.dto.compilation.UpdateCompilationRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CompilationService {
    CompilationDto createNewCompilation(NewCompilationDto newCompilationDto);

    List<CompilationDto> getAllCompilations(Optional<Boolean> pinned, Pageable pageable);

    CompilationDto getCompilationById(Long compilationId);

    CompilationDto updateCompilationById(Long compilationId, UpdateCompilationRequest updateCompilationRequest);

    void deleteCompilationById(Long compilationId);
}