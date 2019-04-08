package nextstep.dto;

import nextstep.domain.Question;

import java.util.List;

public class QuestionDTO {
    private long id;

    private String title;

    private String contents;

    private UserDTO writer;

    private List<AnswerDTO> answers;

    private boolean deleted = false;

    private int answerSize;

    public QuestionDTO() {}

    public QuestionDTO(Question question, UserDTO writer, List<AnswerDTO> answers, boolean deleted) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.contents = question.getContents();
        this.writer = writer;
        this.answers = answers;
        this.deleted = deleted;
        this.answerSize = this.answers.size();
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
}
