package nextstep.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    Iterable<Question> findByDeleted(boolean deleted);
}
