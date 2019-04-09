package nextstep.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findAllByDeleted(boolean deleted, Pageable pageable);

    Optional<Question> findByIdAndDeletedFalse(long id);

    Iterable<Question> findByDeleted(boolean deleted);
}
