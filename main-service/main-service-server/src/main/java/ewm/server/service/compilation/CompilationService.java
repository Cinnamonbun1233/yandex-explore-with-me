package ewm.server.service.compilation;

import ewm.server.dto.compilation.CompilationDto;
import ewm.server.dto.compilation.NewCompilationDto;
import ewm.server.dto.compilation.UpdateCompilationRequest;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request);

    CompilationDto getCompilationById(Long compId);
}