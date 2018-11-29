package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {

    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() {
        return title;
    }

    public Question setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public Question setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public User getWriter() {
        return writer;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void update(final User loginUser, final Question question) {

        if (isDeleted()) {
            throw new CannotUpdateException("Deleted questions.");
        }

        if (!isOwner(loginUser)) {
            throw new CannotUpdateException("Not the author of the question");
        }

        this.title = question.title;
        this.contents = question.contents;
    }

    public void delete(final User loginUser) throws CannotDeleteException {

        if (isDeleted()) {
            throw new CannotDeleteException("Deleted questions.");
        }

        if (!isOwner(loginUser)) {
            throw new CannotDeleteException("Not the author of the question");
        }

        this.deleted = true;
    }

    public boolean hasAnswers() {
        return getAnswersCount() > 0;
    }

    private long getAnswersCount() {
        return this.answers.stream().filter(answer -> !answer.isDeleted()).count();
    }

    public boolean eqTitleAndContents(final Question question) {
        return this.eqTitle(question) && this.eqContent(question);
    }

    private boolean eqTitle(final Question question) {
        return this.title.equals(question.getTitle());
    }

    private boolean eqContent(final Question question) {
        return this.contents.equals(question.getContents());
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }

}
