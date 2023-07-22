package ewm.server.mapper.compilation;

import ewm.client.StatsClient;
import ewm.dto.StatsResponseDto;
import ewm.server.dto.compilation.CompilationDto;
import ewm.server.dto.compilation.NewCompilationDto;
import ewm.server.mapper.event.EventMapper;
import ewm.server.model.compilation.Compilation;

import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class CompilationMapper {
    private static final BiFunction<Long, StatsClient, Integer> getViewsOfEventFunc = (id, statsClient) ->
    {
        StatsResponseDto stats = statsClient.getStats("2000-01-01 00:00:00",
                "2100-01-01 00:00:00",
                new String[]{String.format("/events/%d", id)}, "true").blockFirst();
        return stats == null ? 0 : stats.getHits().intValue();
    };

    public static Compilation mapDtoToModel(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(dto.getPinned() != null && dto.getPinned());
        compilation.setTitle(dto.getTitle());
        return compilation;
    }

    public static CompilationDto mapModelToDto(Compilation model, StatsClient client) {
        return CompilationDto.builder()
                .id(model.getId())
                .title(model.getTitle())
                .pinned(model.getPinned())
                .events(model.getEvents().stream()
                        .map(e -> EventMapper.mapModelToShortDto(e, getViewsOfEventFunc.apply(e.getId(), client)))
                        .collect(Collectors.toList()))
                .build();
    }
}