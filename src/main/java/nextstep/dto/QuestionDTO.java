package nextstep.dto;

import nextstep.domain.DeleteHistory;
import nextstep.domain.Question;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionDTO {
    private long id;

    private String title;

    private String contents;

    private UserDTO writer;

    private List<AnswerDTO> answers;

    private boolean deleted = false;

    private int answerSize;

    private List<DeleteHistory> deleteHistories;

    private String createAt;

    private String updateAt;

    public QuestionDTO() {}

    public QuestionDTO(Question question) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.contents = question.getContents();
        this.writer = new UserDTO(question.getWriter());
        this.answers = question.getAnswers().stream()
                .map(AnswerDTO::new)
                .collect(Collectors.toList());
        this.deleted = question.isDeleted();
        this.answerSize = this.answers.size();
        this.deleteHistories = question.getDeleteHistories();
        this.deleteHistories.size();
        this.createAt = question.getFormattedCreateDate();
        this.updateAt = question.getFormattedModifiedDate();
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public UserDTO getWriter() {
        return writer;
    }

    public List<AnswerDTO> getAnswers() {
        return answers;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public int getAnswerSize() {
        return answerSize;
    }

    public long getId() {
        return id;
    }

    AnswerDTO get(int index) {
        return answers.get(index);
    }

    public List<DeleteHistory> getDeleteHistories() {
        return deleteHistories;
    }

    public String getCreateAt() {
        return createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }
}
