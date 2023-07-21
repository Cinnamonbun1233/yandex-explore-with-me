package ewm.server.mapper.compilation;

import ewm.server.dto.compilation.CompilationDto;
import ewm.server.dto.compilation.NewCompilationDto;
import ewm.server.mapper.event.EventMapper;
import ewm.server.model.compilation.Compilation;

import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation mapDtoToModel(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(dto.getPinned());
        compilation.setTitle(dto.getTitle());
        return compilation;
    }

    public static CompilationDto mapModelToDto(Compilation model) {
        return CompilationDto.builder()
                .id(model.getId())
                .title(model.getTitle())
                .pinned(model.getPinned())
                .events(model.getEvents().stream()
                        .map(EventMapper::mapModelToShortDto)
                        .collect(Collectors.toList()))
                .build();
    }
}