package ewm.server.service.user;

import ewm.server.model.user.User;

import java.util.List;

public interface UserService {
    User addUser(User user);

    List<User> getUsers(Long[] ids, Integer from, Integer size);

    void deleteUserById(Long userId);
}