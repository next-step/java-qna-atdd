package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {

    @Embedded
    private QuestionBody questionBody;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @Embedded
    private Answers answers = new Answers();

    private boolean deleted = false;

    public Question() {
    }

    public Question(QuestionBody questionBody) {
        this.questionBody = questionBody;
    }

    public QuestionBody getQuestionBody() {
        return questionBody;
    }

    public User getWriter() {
        return writer;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public Answers getAnswers() {
        return answers;
    }

    public void addAnswer(Answer answer) {
        answers.addAnswer(answer, this);
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void update(User loginUser, QuestionBody updatedQuestionBody) {
        if(!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        questionBody.update(updatedQuestionBody);
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        if (!isOwner(loginUser)) {
            throw new CannotDeleteException("작성자만 삭제 가능합니다.");
        }

        List<DeleteHistory> deleteHistories = new ArrayList<>();
        deleteHistories.add(setDeleteHistory(loginUser));
        deleteHistories.addAll(answers.delete(loginUser));
        this.deleted = true;

        return deleteHistories;
    }

    private DeleteHistory setDeleteHistory(User loginUser) {
        return DeleteHistory.of(ContentType.QUESTION, getId(), loginUser);
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Question question = (Question) o;
        return deleted == question.deleted &&
                Objects.equals(questionBody, question.questionBody) &&
                Objects.equals(writer, question.writer) &&
                Objects.equals(answers, question.answers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), questionBody, writer, answers, deleted);
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionBody=" + questionBody +
                ", writer=" + writer +
                ", answers=" + answers +
                ", deleted=" + deleted +
                '}';
    }
}
