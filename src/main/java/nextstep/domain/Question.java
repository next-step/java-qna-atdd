package nextstep.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
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

    public Question writeBy(User loginUser) {
        this.writer = loginUser;
        return this;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isNotOwner(User loginUser) {
        return !writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void update(User loginUser, Question target) {
        if (isNotOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        if (isDeleted()) {
            throw new UnAuthorizedException();
        }

        this.title = target.title;
        this.contents = target.contents;
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        if (isNotOwner(loginUser)) {
            throw new UnAuthorizedException("다른 사람이 작성한 질문");
        }

        if (isDeleted()) {
            throw new CannotDeleteException("이미 지워진 질문");
        }

        List<DeleteHistory> deleteHistories = answers.delete(loginUser);
        this.deleted = true;
        deleteHistories.add(new DeleteHistory(ContentType.QUESTION, getId(), loginUser, LocalDateTime.now()));

        return deleteHistories;
    }

    public boolean equalsTitleAndContents(Question target) {
        if (Objects.isNull(target)) {
            return false;
        }

        return title.equals(target.title) &&
                contents.equals(target.contents);
    }

    public Answers getAnswers() {
        return answers;
    }

    public Answer getAnswer(long answerId) {
        return answers.getAnswer(answerId);
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents
                + ", writer=" + writer + "]";
    }
}
