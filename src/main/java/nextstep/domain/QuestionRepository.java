package nextstep.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Question findFirstByOrderByIdDesc();

    Iterable<Question> findByDeleted(boolean deleted);
}
