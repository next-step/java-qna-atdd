package nextstep.dto;

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

    public QuestionDTO(long id, String title, String contents, UserDTO writer, List<AnswerDTO> answers, boolean deleted) {
        this.id = id;
        this.title = title;
        this.contents = contents;
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
}
