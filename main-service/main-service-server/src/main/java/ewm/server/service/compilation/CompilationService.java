package ewm.server.service.compilation;

import ewm.server.dto.compilation.CompilationDto;
import ewm.server.dto.compilation.NewCompilationDto;
import ewm.server.dto.compilation.UpdateCompilationRequest;

import java.util.List;
import java.util.Optional;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request);

    CompilationDto getCompilationById(Long compId);

    List<CompilationDto> getAllCompilations(Optional<Boolean> pinned, int from, int size);

    void deleteCompilation(Long compId);
}