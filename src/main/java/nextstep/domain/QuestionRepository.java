package nextstep.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    Iterable<Question> findByDeleted(boolean deleted);
}
