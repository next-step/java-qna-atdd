package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
public class Answer extends AbstractEntity implements UrlGeneratable {
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_writer"))
    private User writer;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_to_question"))
    private Question question;

    @Size(min = 5)
    @Lob
    private String contents;

    private boolean deleted = false;

    public Answer() {
    }

    public Answer(User writer, String contents) {
        this.writer = writer;
        this.contents = contents;
    }

    public Answer(Long id, User writer, Question question, String contents) {
        super(id);
        this.writer = writer;
        this.question = question;
        this.contents = contents;
        this.deleted = false;
    }

    public Answer(Long id, User writer, Question question, String contents, boolean deleted) {
        super(id);
        this.writer = writer;
        this.question = question;
        this.contents = contents;
        this.deleted = deleted;
    }

    public User getWriter() {
        return writer;
    }

    public Question getQuestion() {
        return question;
    }

    public String getContents() {
        return contents;
    }

    public Answer setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public void toQuestion(Question question) {
        this.question = question;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void update(User user, Answer updatedAnswer) {
        if (!isOwner(user)) {
            throw new UnAuthorizedException("The owner doesn't match");
        }

        if (question.isDeleted()) {
            throw new IllegalStateException("It's deleted question");
        }

        if (isDeleted()) {
            throw new IllegalStateException("It's deleted answer");
        }

        this.contents = updatedAnswer.contents;
    }

    public DeleteHistory delete(User loginUser) throws CannotDeleteException {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("The owner doesn't match");
        }

        if (question.isDeleted()) {
            throw new CannotDeleteException("It's deleted question");
        }

        if (isDeleted()) {
            throw new CannotDeleteException("This answer has already deleted");
        }

        return deleteAnswer(loginUser);
    }

    @Override
    public String generateUrl() {
        return String.format("%s/answers/%d", question.generateRestUrl(), getId());
    }

    @Override
    public String toString() {
        return "Answer [id=" + getId() + ", writer=" + writer + ", contents=" + contents + "]";
    }

    private DeleteHistory deleteAnswer(User loginUser) {
        this.deleted = true;

        return new DeleteHistory(ContentType.ANSWER, getId(), loginUser);
    }
}

