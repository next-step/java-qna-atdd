package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.UserTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.test.AcceptanceTest;

import static nextstep.domain.QuestionTest.newQuestion;
import static nextstep.web.ApiQuestionAcceptanceTest.API_QUESTIONS;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);

    @Test
    public void create() {
        String questionLocation = createQuestionLocation();
        Answer newAnswer = new Answer(UserTest.JAVAJIGI, "답변");

        String location = createResource(basicAuthTemplate(), questionLocation, newAnswer);
        Answer createdAnswer = getResource(template(), location, Answer.class);
        softly.assertThat(createdAnswer).isNotNull();
        softly.assertThat(createdAnswer.equalsContents(newAnswer));
    }


    public String createQuestionLocation() {
        String path = createResource(basicAuthTemplate(), API_QUESTIONS, newQuestion("default 질문 제목", "default 질문 내용"));
        return String.format("%s/answers", path);
    }

}
