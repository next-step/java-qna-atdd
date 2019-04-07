package nextstep.domain.dto;

import nextstep.domain.entity.User;

public class AnswerResponseDto {

    private long id;

    private User writer;

    private long questionId;

    private String contents;

    private boolean deleted = false;

    public long getId() {
        return id;
    }

    public AnswerResponseDto setId(long id) {
        this.id = id;
        return this;
    }

    public User getWriter() {
        return writer;
    }

    public AnswerResponseDto setWriter(User writer) {
        this.writer = writer;
        return this;
    }

    public long getQuestionId() {
        return questionId;
    }

    public AnswerResponseDto setQuestionId(long questionId) {
        this.questionId = questionId;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public AnswerResponseDto setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public AnswerResponseDto setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public String generateApiUrl() {
        return String.format("/api/questions/%d/answers/%d", questionId, id);
    }

    public String generateUrl() {
        return String.format("/questions/%d/answers/%d", questionId, id);
    }
}
