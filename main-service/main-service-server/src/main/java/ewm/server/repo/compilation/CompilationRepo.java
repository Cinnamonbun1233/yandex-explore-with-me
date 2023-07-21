package ewm.server.repo.compilation;

import ewm.server.model.compilation.Compilation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompilationRepo extends JpaRepository<Compilation, Long> {
}