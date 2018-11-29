package nextstep.domain;

import java.util.ArrayList;
import java.util.List;

public class DeleteHistories {
    private List<DeleteHistory> deleteHistories = new ArrayList<>();


    public List<DeleteHistory> getDeleteHistories() {
        return this.deleteHistories;
    }

    public void addDeleteHistory(DeleteHistory deleteHistory) {
        this.deleteHistories.add(deleteHistory);
    }

    public Integer askSize() {
        return deleteHistories.size();
    }
}
