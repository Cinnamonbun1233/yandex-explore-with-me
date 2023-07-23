package ewm.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsRequestDto {
    String app;
    String uri;
    String ip;
    String timestamp;
}