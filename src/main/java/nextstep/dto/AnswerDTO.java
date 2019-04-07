package nextstep.dto;

import nextstep.domain.Answer;

public class AnswerDTO {
    private long id;

    private String contents;

    private boolean deleted = false;

    public AnswerDTO() {
    }

    public AnswerDTO(Answer answer) {
        this.id = answer.getId();
        this.contents = answer.getContents();
        this.deleted = answer.isDeleted();
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
}
