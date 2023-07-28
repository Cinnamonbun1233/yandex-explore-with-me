package ewm.server.service.user;

import ewm.server.dto.user.NewUserRequest;
import ewm.server.dto.user.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserDto createNewUser(NewUserRequest newUserRequest);

    List<UserDto> getAllUsers(List<Long> ids, Pageable pageable);

    void deleteUserById(Long userId);
}