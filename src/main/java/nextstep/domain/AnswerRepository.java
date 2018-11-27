package nextstep.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Optional<Answer> findAnswerByIdAndDeleted(Long id, boolean deleted);

}
