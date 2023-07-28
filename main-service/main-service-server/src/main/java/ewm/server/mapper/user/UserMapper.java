package ewm.server.mapper.user;

import ewm.server.dto.user.NewUserRequest;
import ewm.server.dto.user.UserDto;
import ewm.server.dto.user.UserShortDto;
import ewm.server.model.user.User;

public class UserMapper {
    public static UserShortDto mapModelToShortDto(User model) {
        return UserShortDto.builder()
                .id(model.getUserId())
                .name(model.getName())
                .build();
    }

    public static User mapDtoToModel(NewUserRequest dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }

    public static UserDto mapModelToDto(User model) {
        return UserDto.builder()
                .id(model.getUserId())
                .name(model.getName())
                .email(model.getEmail())
                .build();
    }
}