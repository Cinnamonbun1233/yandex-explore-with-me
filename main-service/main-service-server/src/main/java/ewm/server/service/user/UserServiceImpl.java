package ewm.server.service.user;

import ewm.server.dto.user.NewUserRequest;
import ewm.server.dto.user.UserDto;
import ewm.server.exception.user.UserNotFoundException;
import ewm.server.mapper.user.UserMapper;
import ewm.server.repo.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    @Transactional
    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        return UserMapper.mapModelToDto(userRepo.save(UserMapper.mapDtoToModel(newUserRequest)));
    }

    private List<UserDto> getAllUsers(Pageable pageable) {
        return userRepo
                .findAll(pageable)
                .getContent()
                .stream()
                .map(UserMapper::mapModelToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        return ids == null ? getAllUsers(pageable) : getUsersByIds(ids, pageable);
    }

    private List<UserDto> getUsersByIds(List<Long> ids, Pageable pageable) {
        return userRepo.findAllByUserIdIn(ids, pageable).getContent().stream()
                .map(UserMapper::mapModelToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long userId) {
        checkIfUserExists(userId);
        userRepo.deleteById(userId);
    }

    private void checkIfUserExists(Long userId) {
        if (userRepo.findById(userId).isEmpty()) {
            throw new UserNotFoundException(String.format("User %d not found", userId));
        }
    }
}