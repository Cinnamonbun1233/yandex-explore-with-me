package ewm.server.mapper.user;

import ewm.server.dto.user.NewUserRequest;
import ewm.server.dto.user.UserDto;
import ewm.server.dto.user.UserShortDto;
import ewm.server.model.user.User;

public class UserMapper {
    public static UserShortDto userToUserShortDto(User user) {

        return UserShortDto
                .builder()
                .id(user.getUserId())
                .name(user.getName())
                .build();
    }

    public static User newUserRequestToUser(NewUserRequest newUserRequest) {

        User user = new User();

        user.setName(newUserRequest.getName());
        user.setEmail(newUserRequest.getEmail());

        return user;
    }

    public static UserDto userToUserDto(User user) {

        return UserDto
                .builder()
                .id(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}