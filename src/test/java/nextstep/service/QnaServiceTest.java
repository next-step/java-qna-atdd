package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.QuestionPermissionException;
import nextstep.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SuppressWarnings({"SpellCheckingInspection", "NonAsciiCharacters"})
@RunWith(MockitoJUnitRunner.Silent.class)
public class QnaServiceTest extends BaseTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void 질문_등록() {
        qnaService.create(
                createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com"),
                new Question("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다.")
        );
    }

    @Test
    public void 내가_작성한_질문_조회() {

        final User user = createUser("javajigi", "test", "자바지기", "javajigi@slipp.net");
        final Question question1 = createUserAndQuestion(user, "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다.");
        final Question question2 = createUserAndQuestion(user, "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다.");

        when(questionRepository.findById(question1.getId())).thenReturn(Optional.of(question1));

        assertThat(qnaService.findByUserAndId(user, question1.getId())).isEqualTo(question2);
    }

    @Test(expected = QuestionPermissionException.class)
    public void 내가_작성하지_않은_질문_조회() {

        final User write = createUser("javajigi", "test", "자바지기", "javajigi@slipp.net");
        final Question question1 = createUserAndQuestion(write, "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다.");

        when(questionRepository.findById(question1.getId())).thenReturn(Optional.of(question1));

        qnaService.findByUserAndId(createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com"), question1.getId());
    }

    @Test
    public void 질문_수정_성공() {

        final User user = createUser("javajigi", "test", "자바지기", "javajigi@slipp.net");
        final Question question1 = createUserAndQuestion(user, "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다.");
        final Question question2 = createUserAndQuestion(user, "국내에서 활성화되기 힘든 이유는 뭘까? Ruby on Rails와 Play가", "ROR는 금방 가라 앉았다.");

        when(questionRepository.findById(question1.getId())).thenReturn(Optional.of(question1));

        qnaService.update(user, question1.getId(), question2);

        assertThat(question1.getTitle()).isEqualTo(question2.getTitle());
        assertThat(question1.getContents()).isEqualTo(question2.getContents());
    }

    @Test(expected = CannotUpdateException.class)
    public void 등록자가_아닌데_질문_수정시() {

        final User write = createUser("javajigi", "test", "자바지기", "javajigi@slipp.net");
        final Question question1 = createUserAndQuestion(write, "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다.");
        final Question question2 = createUserAndQuestion(write, "국내에서 활성화되기 힘든 이유는 뭘까? Ruby on Rails와 Play가", "ROR는 금방 가라 앉았다.");
        final User user = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");

        when(questionRepository.findById(question1.getId())).thenReturn(Optional.of(question1));

        qnaService.update(user, question1.getId(), question2);

        assertThat(question1.getTitle()).isEqualTo(question2.getTitle());
        assertThat(question1.getContents()).isEqualTo(question2.getContents());
    }

    @Test(expected = CannotUpdateException.class)
    public void 삭제된_질문_수정시() throws CannotDeleteException {

        final User write = createUser("javajigi", "test", "자바지기", "javajigi@slipp.net");
        final Question question1 = createUserAndQuestion(write, "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다.");
        final Question question2 = createUserAndQuestion(write, "국내에서 활성화되기 힘든 이유는 뭘까? Ruby on Rails와 Play가", "ROR는 금방 가라 앉았다.");

        when(questionRepository.findById(question1.getId())).thenReturn(Optional.of(question1));

        question1.delete(write);
        qnaService.update(write, question1.getId(), question2);

        assertThat(question1.getTitle()).isEqualTo(question2.getTitle());
        assertThat(question1.getContents()).isEqualTo(question2.getContents());
    }

    @Test
    public void 질문_삭제_성공() throws CannotDeleteException {

        final User user = createUser("javajigi", "test", "자바지기", "javajigi@slipp.net");
        final Question question = createUserAndQuestion(user, "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다.");

        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        qnaService.delete(user, 1);

        assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void 삭제된_질문_다시_삭제_시도() throws CannotDeleteException {

        final User user = createUser("javajigi", "test", "자바지기", "javajigi@slipp.net");
        final Question question = createUserAndQuestion(user, "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다.");

        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        qnaService.delete(user, 1);
        qnaService.delete(user, 1);
    }

    @Test(expected = CannotDeleteException.class)
    public void 작성자가_아닌데_질문_삭제_시도() throws CannotDeleteException {

        final User write = createUser("javajigi", "test", "자바지기", "javajigi@slipp.net");
        final User user = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        final Question question = createUserAndQuestion(write, "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다.");

        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        qnaService.delete(user, 1);
    }

    @Test
    public void 질문에_답변_등록() {

        final User user = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");

        final Question question = new Question("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다.");
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        qnaService.create(user, question);
        assertThat(question.hasAnswers()).isFalse();

        question.addAnswer(new Answer(user, "좋은 책이네요."));
        assertThat(qnaService.findByUserAndId(user, question.getId()).hasAnswers()).isTrue();
    }

    @Test
    public void 질문에_답변_삭제() {

        final User user = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");

        final Question question = new Question("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다.");
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        final Answer answer = new Answer(user, "좋은 책이네요.");
        answer.setId(0);
        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));

        question.addAnswer(answer);
        qnaService.create(user, question);
        assertThat(question.hasAnswers()).isTrue();

        qnaService.deleteAnswer(user, answer.getId());
        assertThat(qnaService.findByUserAndId(user, question.getId()).hasAnswers()).isFalse();
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문에_달린_다른_사람의_답변_삭제() {

        final User user = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");

        final Question question = new Question("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다.");
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        final Answer answer = new Answer(createUser("javajigi", "test", "자바지기", "javajigi@slipp.net"), "좋은 책이네요.");
        answer.setId(0);
        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));

        question.addAnswer(answer);
        qnaService.create(user, question);
        assertThat(question.hasAnswers()).isTrue();

        qnaService.deleteAnswer(user, answer.getId());
        assertThat(question.hasAnswers()).isTrue();
    }

    @Test
    public void 질문에_답변_수정() {

        final User user = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");

        final Question question = new Question("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다.");
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        final Answer answer = new Answer(user, "좋은 책이네요.");
        answer.setId(0);
        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));

        question.addAnswer(answer);
        qnaService.create(user, question);
        assertThat(question.hasAnswers()).isTrue();

        final Answer updateAnswer = new Answer(user, "좋은 책이네요!!!");
        qnaService.updateAnswer(user, updateAnswer);
        assertThat(answerRepository.findById(answer.getId()).filter(a -> a.eqContents(updateAnswer)).isPresent()).isTrue();

    }

    @Test(expected = CannotUpdateException.class)
    public void 질문에_다른_사람의_답변_수정() {

        final User user = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");

        final Question question = new Question("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다.");
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        final Answer answer = new Answer(user, "좋은 책이네요.");
        answer.setId(0);
        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));

        question.addAnswer(answer);
        qnaService.create(user, question);
        assertThat(question.hasAnswers()).isTrue();

        final Answer updateAnswer = new Answer(user, "좋은 책이네요!!!");
        qnaService.updateAnswer(createUser("javajigi", "test", "자바지기", "javajigi@slipp.net"), updateAnswer);
    }

    private Question createUserAndQuestion(final User user, final String title, final String contents) {
        final Question question1 = new Question(title, contents);
        question1.setId(1);
        question1.writeBy(user);
        return question1;
    }

    private User createUser(final String userId, final String password, final String name, final String email) {
        return new User(userId, password, name, email);
    }

}