package nextstep.web;

import nextstep.domain.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.test.AcceptanceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    public static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    @Test
    public void create() {
        User loginUser = defaultUser();
        Question question = QuestionTest.newQuestion(loginUser);
        String location = createResource("/api/questions", question, loginUser);
        String contents ="답변이니다다다";
        String resource = createResource(location +"/answers", contents, loginUser);
        assertThat(resource).isNotNull();

        Question newQuestion = getResource(location, Question.class, loginUser);
        Answer answer = newQuestion.getAnswers().get(0);
        softly.assertThat(answer).isNotNull();
        softly.assertThat(answer.getContents()).isEqualTo(contents);
    }

    @Test
    public void delete() {
        User loginUser = defaultUser();

        String location = "/api/questions/1";
        String contents ="답변이니다다다2";

        createResource(location +"/answers", contents, loginUser);
        Question question = getResource(location, Question.class, loginUser);

        Answers answers = question.getAnswers();
        Answer answer = answers.get(answers.size()-1);

        softly.assertThat(question).isNotNull();
        softly.assertThat(answer).isNotNull();

        delete(location+"/answers/"+answer.getId(), loginUser);
        Question updateQuestion = getResource(location, Question.class, loginUser);

        softly.assertThat(updateQuestion.getAnswers().size()).isNotEqualTo(answers.size());
    }
}
