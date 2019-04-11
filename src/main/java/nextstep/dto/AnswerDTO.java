package nextstep.dto;

import nextstep.domain.Answer;
import nextstep.domain.DeleteHistory;
import nextstep.domain.Question;

import java.util.List;

public class AnswerDTO {
    private long id;

    private String contents;

    private boolean deleted = false;

    private Question question;

    private List<DeleteHistory> histories;

    private String createAt;

    private String updateAt;

    public AnswerDTO() {
    }

    public AnswerDTO(Answer answer) {
        this.id = answer.getId();
        this.contents = answer.getContents();
        this.deleted = answer.isDeleted();
        this.histories = answer.getDeleteHistories();
        histories.size();
        this.createAt = answer.getFormattedCreateDate();
        this.updateAt = answer.getFormattedModifiedDate();
    }

    public AnswerDTO(long id, String contents, boolean deleted) {
        this.id = id;
        this.contents = contents;
        this.deleted = deleted;
    }

    public long getId() {
        return id;
    }

    public String getContents() {
        return contents;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Question getQuestion() {
        return question;
    }

    public List<DeleteHistory> getHistories() {
        return histories;
    }

    public String getCreateAt() {
        return createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }
}
