package nextstep.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.CannotDeleteException;
import nextstep.UnAuthenticationException;
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

    @Embedded
    @JsonProperty
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

    public Answer getAnswer(int index) {
        return this.answers.get(index);
    }

    public void addAnswer(Answer answer) {
        this.answers.addAnswer(this, answer);
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

    public Question update(Question updatedQuestion) {
        if(!isOwner(updatedQuestion.writer)) {
            throw new UnAuthorizedException();
        }
        this.title = updatedQuestion.title;
        this.contents = updatedQuestion.contents;
        return this;
    }

    public List<DeleteHistory> delete(User loginUser) {
        DeleteHistory deleteHistory = deleteQuestion(loginUser);

        List<DeleteHistory> histories = new ArrayList<>();
        histories.add(deleteHistory);

        List<DeleteHistory> answerDeleteHistories = this.answers.deleteAll(loginUser);
        histories.addAll(answerDeleteHistories);

        return histories;
    }

    public DeleteHistory deleteQuestion(User loginUser) {
        if(!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        this.deleted = true;
        return new DeleteHistory(ContentType.QUESTION, this.getId(), this.writer, LocalDateTime.now());
    }

    public boolean equalsTitleAndContents(Question body) {
        if(!this.title.equals(body.title)) {
            return false;
        }
        if(!this.contents.equals(body.contents)) {
            return false;
        }
        return true;
    }

    public int answerCount() {
        return this.answers.size();
    }

    public void addAllAnswer(Collection<Answer> answers) {
        this.answers.addAll(answers, this);
    }

    @JsonIgnore
    public boolean isAllAnswersDeleted() {
        return this.answers.isAllDeleted();
    }


}