package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Embedded
    private Answers answers = new Answers();

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

    public void update(User loginUser, Question target) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        this.title = target.title;
        this.contents = target.contents;
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        System.out.println("#############");
        System.out.println(loginUser);
        System.out.println(this);
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        if (isDeleted() || answers.hasOtherUsersAnswer(loginUser)) {
            System.out.println("@@@@@@ 1: " + isDeleted());
            System.out.println("@@@@@@ 2: " + answers.hasOtherUsersAnswer(loginUser));
            throw new CannotDeleteException("");
        }

        this.deleted = true;
        List<DeleteHistory> deleteHistories = answers.deleteAll(loginUser);
        deleteHistories.add(DeleteHistory.fromQuestion(loginUser, this));

        return deleteHistories;
    }

    public boolean equalsTitleAndContents(Question target) {
        if (Objects.isNull(target)) {
            return false;
        }

        return title.equals(target.title) &&
                contents.equals(target.contents);
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
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
