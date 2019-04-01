package nextstep.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Iterable<Question> findByDeleted(boolean deleted);
    Optional<Question> findFirstByTitleAndContents(String title, String contents);
    Optional<Question> findByIdAndWriter(Long id, User writer);
    void deleteByIdAndWriter(Long id, User writer);
}
