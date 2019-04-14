package nextstep.domain.entity;

import nextstep.UnAuthorizedException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Question(long id, String title, String contents, User writer) {
        super(id);
        this.title = title;
        this.contents = contents;
        this.writer = writer;
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

    public List<Answer> getAnswers() {
        return answers;
    }

    public Question addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
        return this;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Question modify(User loginUser, Question updatedQuestion) throws UnAuthorizedException {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        this.title = updatedQuestion.getTitle();
        this.contents = updatedQuestion.getContents();
        return this;
    }

    public Question delete(User loginUser) throws UnAuthorizedException {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        if (answers.isEmpty()) {
            this.deleted = true;
            return this;
        }

        if (findDifferentUser(loginUser)) {
            throw new UnAuthorizedException();
        }

        deleteAnswers(loginUser);

        this.deleted = true;
        return this;
    }

    private void deleteAnswers(User loginUser) {
        for (Answer answer : answers) {
            answer.delete(loginUser);
        }
    }

    private boolean findDifferentUser(User loginUser) {
        return answers.stream().allMatch(answer -> !answer.isOwner(loginUser));
    }

    public String generateApiUrl() {
        return String.format("/api/questions/%d", getId());
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
