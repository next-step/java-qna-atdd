package nextstep.domain.repository;

import nextstep.domain.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Iterable<Question> findByDeleted(boolean deleted);
}
