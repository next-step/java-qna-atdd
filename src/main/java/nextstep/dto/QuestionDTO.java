package nextstep.dto;

import nextstep.domain.Question;

public class QuestionDTO {
    private Question question;
    private int answerSize;

    public QuestionDTO() {}

    public QuestionDTO(Question question, int answerSize) {
        this.question = question;
        this.answerSize = answerSize;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setAnswerSize(int answerSize) {
        this.answerSize = answerSize;
    }

    public Question getQuestion() {
        return question;
    }

    public int getAnswerSize() {
        return answerSize;
    }
}
