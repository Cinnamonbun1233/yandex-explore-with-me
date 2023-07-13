package ewm_dto.domain_dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsRequestDto {
    String app;
    String uri;
    String ip;
    String timestamp;
}