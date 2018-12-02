package nextstep.domain;

import java.util.ArrayList;
import java.util.List;

public class DeleteHistories {
    List<DeleteHistory> deleteHistories = new ArrayList<>();

    public DeleteHistories(List<DeleteHistory> deleteHistories) {
        this.deleteHistories = deleteHistories;
    }

    public void deleteQuestion(Question question, User loginUser) {
        deleteHistories.add(DeleteHistory.of(ContentType.QUESTION, question.getId(), loginUser));
    }

    public List<DeleteHistory> getDeleteHistories() {
        return deleteHistories;
    }
}
