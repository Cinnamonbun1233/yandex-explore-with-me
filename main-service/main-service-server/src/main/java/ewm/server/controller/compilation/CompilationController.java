package ewm.server.controller.compilation;

import ewm.server.dto.compilation.CompilationDto;
import ewm.server.dto.compilation.NewCompilationDto;
import ewm.server.dto.compilation.UpdateCompilationRequest;
import ewm.server.service.compilation.CompilationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<CompilationDto> createNewCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compilationService.createNewCompilation(newCompilationDto));
    }

    @PatchMapping(value = COMPILATIONS_ADMIN_PATH + "/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable("compId") Long compId,
                                                            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return ResponseEntity.ok().body(compilationService.updateCompilationById(compId, updateCompilationRequest));
    }

    @GetMapping(COMPILATIONS_PUBLIC_PATH + "/{compId}")
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable("compId") Long compId) {
        return ResponseEntity.ok().body(compilationService.getCompilationById(compId));
    }

    @GetMapping(COMPILATIONS_PUBLIC_PATH)
    public ResponseEntity<List<CompilationDto>> getAllCompilations(@RequestParam(name = "pinned", required = false) Optional<Boolean> pinned,
                                                                   @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                                   @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);

        return ResponseEntity.ok().body(compilationService.getAllCompilations(pinned, pageable));
    }

    @DeleteMapping(COMPILATIONS_ADMIN_PATH + "/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable("compId") Long compId) {
        compilationService.deleteCompilationById(compId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}