package nextstep.domain.entity;

import nextstep.domain.ContentType;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteHistoryTest {
    @Test
    public void 등록_확인() {
        User user = new User(20L, "user","pass","name", "email");
        Answer answer = new Answer(3L, user, null, "답변글");
        DeleteHistory deleteHistory = new DeleteHistory(1L, ContentType.ANSWER, answer.getId(), user);
        assertThat(deleteHistory.getDeletedBy()).isEqualTo(user);
        assertThat(deleteHistory.getContentType()).isEqualTo(ContentType.ANSWER);
        assertThat(deleteHistory.getId()).isEqualTo(1L);
    }

    @Test
    public void 삭제이력_리스트_확인() {
        User user = new User(20L, "user","pass","name", "email");
        Answer answerOne = new Answer(1L, user, null, "답변글");
        Answer answerTwo = new Answer(2L, user, null, "답변글");
        Question question = new Question(1L, "제목입니다.","내용입니다.", user);
        question.addAnswer(answerOne);
        question.addAnswer(answerTwo);

        List<DeleteHistory> deleteHistories = DeleteHistory.toDeleteHistories(question);

        assertThat(deleteHistories.size()).isEqualTo(3);
    }
}