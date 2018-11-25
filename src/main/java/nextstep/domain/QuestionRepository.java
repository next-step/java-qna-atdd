package nextstep.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Iterable<Question> findByDeleted(boolean deleted);
    Optional<Question> findByWriterAndTitle(User writer, String title);
}
