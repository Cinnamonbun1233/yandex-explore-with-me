package ewm.server.controller.compilation;

import ewm.server.dto.compilation.CompilationDto;
import ewm.server.dto.compilation.NewCompilationDto;
import ewm.server.dto.compilation.UpdateCompilationRequest;
import ewm.server.service.compilation.CompilationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class CompilationController {
    private static final String COMPILATIONS_ADMIN_PATH = "/admin/compilations";
    private static final String COMPILATIONS_PUBLIC_PATH = "/compilations";
    private final CompilationService compilationService;

    @Autowired
    public CompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping(value = COMPILATIONS_ADMIN_PATH)
    public ResponseEntity<CompilationDto> addCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compilationService.addCompilation(newCompilationDto));
    }

    @PatchMapping(value = COMPILATIONS_ADMIN_PATH + "/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable("compId") Long compId,
                                                            @Valid @RequestBody UpdateCompilationRequest request) {
        return ResponseEntity.ok().body(compilationService.updateCompilation(compId, request));
    }

    @GetMapping(COMPILATIONS_PUBLIC_PATH + "/{compId}")
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable("compId") Long compId) {
        return ResponseEntity.ok().body(compilationService.getCompilationById(compId));
    }
}