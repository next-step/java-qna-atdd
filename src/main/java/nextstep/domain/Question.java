package nextstep.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.UnAuthorizedException;
import nextstep.dto.QuestionRequest;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {

    @Embedded
    @JsonProperty
    @Valid
    private QuestionPost questionPost;

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
        this(0L, title, contents);
    }

    public Question(Long id, String title, String contents) {
        super(id);
        this.questionPost = new QuestionPost(title, contents);
    }

    public static Question from(QuestionRequest questionRequest) {
        return new Question(questionRequest.getTitle(), questionRequest.getContents());
    }

    public String getTitle() {
        return this.questionPost.getTitle();
    }

    public String getContents() {
        return this.questionPost.getContents();
    }

    public User getWriter() {
        return writer;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public void addAnswer(Answer answer) {
        this.answers.addAnswer(this, answer);
    }

    public Answer getAnswer(int index) {
        return this.answers.get(index);
    }

    public int answerCount() {
        return this.answers.size();
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

        final List<DeleteHistory> deleteAnswerHistories = this.answers.deleteAll(loginUser, this.getId());
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

    public Question update(User loginUser, Question target) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        this.questionPost = target.questionPost;

        return this;
    }

    public Question update(User loginUser, QuestionPost questionPost) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        this.questionPost = questionPost;

        return this;
    }

    public boolean isEqualQuestion(Question other) {
        if (!this.equals(other)) {
            return false;
        }

        return this.questionPost.equals(other.questionPost);
    }

    public boolean containsAnswer(Answer answer) {
        return this.answers.contains(answer);
    }

    public void addAllAnswer(Collection<Answer> answers) {
        this.answers.addAll(answers, this);
    }

    @JsonIgnore
    public boolean isAllAnswerDeleted() {
        return this.answers.isAllDeleted();
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionPost=" + questionPost +
                ", writer=" + writer +
                ", answers=" + answers +
                ", deleted=" + deleted +
                '}';
    }
}
