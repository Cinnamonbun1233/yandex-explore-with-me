package ewm.server.service.user;

import ewm.server.dto.user.NewUserRequest;
import ewm.server.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    void deleteUserById(Long userId);
}