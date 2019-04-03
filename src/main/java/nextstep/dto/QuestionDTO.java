package nextstep.dto;

import nextstep.domain.Question;

public class QuestionDTO {
    private final Question question;
    private final int answerSize;

    public QuestionDTO(Question question, int answerSize) {
        this.question = question;
        this.answerSize = answerSize;
    }

    public Question getQuestion() {
        return question;
    }

    public int getAnswerSize() {
        return answerSize;
    }
}
