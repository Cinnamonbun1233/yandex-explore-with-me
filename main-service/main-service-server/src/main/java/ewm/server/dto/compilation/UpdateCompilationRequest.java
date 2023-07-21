package ewm.server.dto.compilation;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequest {
    List<Long> events;
    Boolean pinned;
    String title;
}