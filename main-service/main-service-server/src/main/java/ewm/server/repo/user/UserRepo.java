package ewm.server.repo.user;

import ewm.server.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Page<User> findAllByUserIdIn(List<Long> ids, Pageable pageable);
}