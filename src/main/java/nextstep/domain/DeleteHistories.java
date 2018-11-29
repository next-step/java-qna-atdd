package nextstep.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DeleteHistories {
    List<DeleteHistory> deleteHistories;

    private DeleteHistories() {
        this.deleteHistories = new ArrayList<>();
    }

    private DeleteHistories(List<DeleteHistory> newHistories) {
        this.deleteHistories = newHistories;
    }

    public List<DeleteHistory> getDeleteHistories() {
        return Collections.unmodifiableList(deleteHistories);
    }

    public DeleteHistories add(DeleteHistory delete) {
        deleteHistories.add(delete);
        return this;
    }

    public static DeleteHistories of(DeleteHistories... deleteHistories) {
        List<DeleteHistory> newHistories = new ArrayList<>();
        for (DeleteHistories histories : deleteHistories) {
            newHistories.addAll(histories.getDeleteHistories());
        }
        return new DeleteHistories(newHistories);
    }

    public static DeleteHistories of() {
        return new DeleteHistories();
    }
}
