package nextstep.domain.entity;

import nextstep.domain.ContentType;
import org.junit.Test;

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
}