package ewm.server.service.user;

import ewm.server.dto.user.NewUserRequest;
import ewm.server.dto.user.UserDto;
import ewm.server.exception.user.UserNotFoundException;
import ewm.server.mapper.user.UserMapper;
import ewm.server.repo.user.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    @Override
    public UserDto addUser(NewUserRequest user) {
        return UserMapper.mapModelToDto(userRepo.save(UserMapper.mapDtoToModel(user)));
    }

    @Override
    public List<UserDto> getUsers(Long[] ids, Integer from, Integer size) {
        Pageable request = PageRequest.of(from > 0 ? from / size : 0, size);
        return ids == null ? getAllUsers(request) : getUsersByIds(ids, request);
    }

    @Transactional
    @Override
    public void deleteUserById(Long userId) {
        checkIfUserExists(userId);
        userRepo.deleteById(userId);
    }

    private void checkIfUserExists(Long userId) {
        if (userRepo.findById(userId).isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
    }

    private List<UserDto> getUsersByIds(Long[] ids, Pageable request) {
        return userRepo.findAllByIdIn(ids, request).getContent().stream()
                .map(UserMapper::mapModelToDto).collect(Collectors.toList());
    }

    private List<UserDto> getAllUsers(Pageable request) {
        return userRepo.findAll(request).getContent().stream()
                .map(UserMapper::mapModelToDto).collect(Collectors.toList());
    }
}