package nextstep.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Iterable<Question> findByDeleted(boolean deleted);

    Optional<Question> findTopByTitle(String title);
}
