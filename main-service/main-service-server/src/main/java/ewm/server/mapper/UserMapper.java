package ewm.server.mapper;

import ewm.server.dto.user.UserShortDto;
import ewm.server.model.user.User;

public class UserMapper {
    public static UserShortDto mapModelToDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}