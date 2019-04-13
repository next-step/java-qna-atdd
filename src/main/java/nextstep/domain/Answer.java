package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
public class Answer extends AbstractEntity implements UrlGeneratable {

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_writer"))
    private User writer;

    @ManyToOne(optional = false)
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

    @Override
    public String generateUrl() {
        return String.format("%s/answers/%d", question.generateUrl(), getId());
    }

    @Override
    public String toString() {
        return "Answer [id=" + getId() + ", writer=" + writer + ", contents=" + contents + "]";
    }

    public boolean isOf(Question question) {
        return this.question.equals(question);
    }

    public void update(User loginUser, String contents) throws CannotUpdateException {
        if (!isOwner(loginUser)) {
            throw new CannotUpdateException("writer should be owner");
        }

        this.contents = contents;
    }

    public DeleteHistory delete(User loginUser) throws CannotDeleteException {
        if (!isOwner(loginUser) || !sameOwnerFromQuestion(loginUser)) {
            throw new CannotDeleteException("writer should be owner and same writer from question");
        }

        this.deleted = true;
        return createDeleteHistory();
    }

    private DeleteHistory createDeleteHistory() {
        return new DeleteHistory(ContentType.ANSWER, getId(), writer);
    }

    private boolean sameOwnerFromQuestion(User loginUser) {
        return question.isOwner(loginUser).isPresent();
    }
}
