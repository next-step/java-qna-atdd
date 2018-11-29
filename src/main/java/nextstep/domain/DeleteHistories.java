package nextstep.domain;

import java.util.List;

public class DeleteHistories {

    private List<DeleteHistory> histories;

    public DeleteHistories(List<DeleteHistory> histories) {
        this.histories = histories;
    }

    public static DeleteHistories create(List<DeleteHistory> histories) {
        return new DeleteHistories(histories);
    }

    public DeleteHistory getOne(int index) {
        return histories.get(index);
    }

    public List<DeleteHistory> getAll() {
        return histories;
    }

    public int size() {
        return histories.size();
    }
}
