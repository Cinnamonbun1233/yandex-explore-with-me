package ewm.server.service;

import ewm.server.model.User;

import java.util.List;

public interface UserService {
    User addUser(User user);

    List<User> getUsers(Long[] ids, Integer from, Integer size);

    void deleteUserById(Long userId);
}