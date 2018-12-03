package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

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

    public DeleteHistory delete(final User loginUser) {

        if (isDeleted()) {
            throw new CannotDeleteException("Deleted answer.");
        }

        if (!isOwner(loginUser)) {
            throw new CannotDeleteException("Not the author of the answer");
        }

        this.deleted = true;

        return createDeleteHistory(loginUser);
    }

    private DeleteHistory createDeleteHistory(final User loginUser) {
        return new DeleteHistory(ContentType.ANSWER, this.getId(), loginUser, LocalDateTime.now());
    }

    public void update(final User loginUser, final Answer answer) {

        if (isDeletedQuestion()) {
            throw new CannotUpdateException("Deleted questions.");
        }

        if (isDeleted()) {
            throw new CannotUpdateException("Deleted answer.");
        }

        if (!isOwner(loginUser)) {
            throw new CannotUpdateException("Not the author of the answer");
        }

        this.contents = answer.contents;
    }

    public boolean isDeletedQuestion() {
        return this.question.isDeleted();
    }

    public boolean eqContents(final Answer answer) {
        return this.contents.equals(answer.getContents());
    }

    @Override
    public String generateUrl() {
        return String.format("%s/answers/%d", question.generateUrl(), getId());
    }

    @Override
    public String toString() {
        return "Answer [id=" + getId() + ", writer=" + writer + ", contents=" + contents + "]";
    }

}
