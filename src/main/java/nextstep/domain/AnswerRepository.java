package nextstep.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
	List<Answer> findAllByQuestionId(long questionId);
	Optional<Answer> findByIdAndDeletedFalse(long answerId);

}
