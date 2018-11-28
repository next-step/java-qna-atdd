package nextstep.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import nextstep.UnAuthorizedException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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

    @JsonManagedReference
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this(0L, title, contents);
    }

    public Question(Long id, String title, String contents) {
        super(id);
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
        if (this.answers.contains(answer)) {
            return;
        }
        answer.toQuestion(this);
        answers.add(answer);
    }

    public List<Answer> getAnswers() {
        return this.answers;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public List<DeleteHistory> delete(User loginUser) {
        List<DeleteHistory> histories = new ArrayList<>();

        DeleteHistory deleteHistory = deleteQuestion(loginUser);
        histories.add(deleteHistory);

        final List<DeleteHistory> deleteAnswerHistories = deleteAllAnswers(loginUser);
        histories.addAll(deleteAnswerHistories);

        return histories;
    }

    private DeleteHistory deleteQuestion(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        this.deleted = true;

        return new DeleteHistory(ContentType.QUESTION, this.getId(), this.writer, LocalDateTime.now());
    }

    private List<DeleteHistory> deleteAllAnswers(User loginUser) {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        for (Answer answer : answers) {
            final DeleteHistory deleteHistory = answer.delete(loginUser, this.getId());
            deleteHistories.add(deleteHistory);
        }
        return deleteHistories;
    }

    public Question update(User loginUser, Question target) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        this.title = target.title;
        this.contents = target.contents;

        return this;
    }

    public boolean isEqualQuestion(Question other) {
        if (!this.equals(other)) {
            return false;
        }

        if (!this.title.equals(other.title)) {
            return false;
        }

        return this.contents.equals(other.contents);
    }

    public boolean containsAnswer(Answer answer) {
        return this.answers.contains(answer);
    }

    public void addAllAnswer(Collection<Answer> answers) {
        for (Answer answer : answers) {
            this.addAnswer(answer);
        }
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
