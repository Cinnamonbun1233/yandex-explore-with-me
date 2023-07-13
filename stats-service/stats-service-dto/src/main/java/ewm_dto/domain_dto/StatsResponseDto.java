package ewm_dto.domain_dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsResponseDto {
    String app;
    String uri;
    Integer hits;
}