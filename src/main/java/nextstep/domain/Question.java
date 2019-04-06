package nextstep.domain;

import nextstep.exception.CannotDeleteException;
import nextstep.exception.ObjectDeletedException;
import nextstep.exception.UnAuthorizedException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        return this.title;
    }

    public Question setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContents() {
        return this.contents;
    }

    public Question setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public User getWriter() {
        return this.writer;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        this.answers.add(answer);
    }

    public Question update(User loginUser, Question target) {
        if (isDeleted()) {
            throw new ObjectDeletedException();
        }
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        this.title = target.title;
        this.contents = target.contents;
        return this;
    }

    public List<DeleteHistory> delete(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        if (!isAnswersSameOwner(loginUser)) {
            throw new CannotDeleteException();
        }

        this.deleted = true;
        List<DeleteHistory> deleteHistories = deleteAllAnswers(loginUser);
        deleteHistories.add(DeleteHistory.generateQuestionHistory(this.getId(), loginUser));

        return deleteHistories;
    }

    private List<DeleteHistory> deleteAllAnswers(User requestUser) {
        return this.answers.stream()
            .map(answer -> answer.delete(requestUser))
            .collect(Collectors.toList());
    }

    private boolean isAnswersSameOwner(User requestUser) {
        return this.answers.stream()
            .allMatch(answer -> answer.isOwner(requestUser));
    }

    public boolean isOwner(User loginUser) {
        return this.writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + this.title + ", contents=" + this.contents + ", writer=" + this.writer + "]";
    }
}
