package nextstep.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nextstep.AlreadyDeletedException;
import nextstep.UnAuthorizedException;
import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.UserTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
	@Mock
	private QuestionRepository questionRepository;
	@Mock
	private AnswerRepository answerRepository;
	@Mock
	private DeleteHistoryService deleteHistoryService;
	@InjectMocks
	private QnaService qnaService;

	private Question question;
	private Answer answer;

	@Before
	public void setUp() throws Exception {
		question = new Question("질문 제목", "내용 블라");
		question.setId(1);
		question.writeBy(UserTest.JAVAJIGI);

		answer = new Answer(1L, UserTest.SANJIGI, question, "답변 내용");
	}

	@Test
	public void createQuestion() {
		when(questionRepository.save(any())).thenReturn(question);

		Question savedQuestion = qnaService.createQuestion(question.getWriter(), question);

		assertThat(savedQuestion.isOwner(question.getWriter())).isTrue();
	}

	@Test
	public void getUpdateQuestionByWriter() {
		when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));

		Question savedQuestion = qnaService.findByIdAndOwnerAndNotDeleted(question.getWriter(), question.getId());

		assertThat(savedQuestion).isEqualTo(question);
	}

	@Test(expected = UnAuthorizedException.class)
	public void getUpdateQuestionByOtherUser() {
		when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));

		qnaService.findByIdAndOwnerAndNotDeleted(UserTest.SANJIGI, question.getId());
	}

	@Test
	public void update() {
		when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));

		Question updatedQuestion = new Question("수정된 제목", "수정된 내용");
		qnaService.updateQuestion(question.getWriter(), question.getId(), updatedQuestion);

		assertThat(question.equalsTitleAndContents(updatedQuestion)).isTrue();
	}

	@Test
	public void deleteByIdAndOwner() throws Exception {
		when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));

		qnaService.deleteQuestion(question.getWriter(), question.getId());

		assertThat(question.isDeleted()).isTrue();
	}

	@Test(expected = AlreadyDeletedException.class)
	public void deleteAlreadyDeleted() throws Exception {
		when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));

		qnaService.deleteQuestion(question.getWriter(), question.getId());
		qnaService.deleteQuestion(question.getWriter(), question.getId());
	}

	@Test(expected = UnAuthorizedException.class)
	public void deleteByIdButNoOwner() throws Exception {
		when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));

		qnaService.deleteQuestion(UserTest.SANJIGI, question.getId());
	}

	@Test
	public void deleteAnswerByIdAndOwner() throws Exception {
		when(answerRepository.findById(any())).thenReturn(Optional.ofNullable(answer));

		qnaService.deleteAnswer(UserTest.SANJIGI, answer.getId());

		assertThat(answer.isDeleted()).isTrue();
	}

	@Test(expected = AlreadyDeletedException.class)
	public void deleteAnswerAlreadyDeleted() throws Exception {
		when(answerRepository.findById(any())).thenReturn(Optional.ofNullable(answer));

		qnaService.deleteAnswer(UserTest.SANJIGI, answer.getId());
		qnaService.deleteAnswer(UserTest.SANJIGI, answer.getId());
	}

	@Test(expected = UnAuthorizedException.class)
	public void deleteAnswerByIdButNoOwner() throws Exception {
		when(answerRepository.findById(any())).thenReturn(Optional.ofNullable(answer));

		qnaService.deleteAnswer(UserTest.JAVAJIGI, answer.getId());
	}
}
