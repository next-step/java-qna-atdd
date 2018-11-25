package nextstep.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
	Iterable<Answer> findAllByQuestionId(long questionId);
	Iterable<Answer> findByDeleted(boolean deleted);
}
