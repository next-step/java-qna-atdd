package nextstep.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.exception.CannotDeleteException;
import nextstep.exception.ObjectDeletedException;
import nextstep.exception.UnAuthorizedException;
import nextstep.web.dto.QuestionRequestDTO;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {

    @Embedded
    @JsonProperty
    private QuestionBody questionBody;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @Embedded
    private Answers answers = new Answers();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.questionBody = new QuestionBody(title, contents);
    }

    public static Question of(QuestionRequestDTO questionRequestDTO) {
        return new Question(questionRequestDTO.getTitle(), questionRequestDTO.getContents());
    }

    public String getTitle() {
        return this.questionBody.getTitle();
    }

    public Question setTitle(String title) {
        this.questionBody.setTitle(title);
        return this;
    }

    public String getContents() {
        return this.questionBody.getContents();
    }

    public Question setContents(String contents) {
        this.questionBody.setContents(contents);
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

        this.questionBody.update(target.getTitle(), target.getContents());
        return this;
    }

    public List<DeleteHistory> delete(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        if (!this.answers.isAnswersSameOwner(loginUser)) {
            throw new CannotDeleteException();
        }

        this.deleted = true;
        List<DeleteHistory> deleteHistories = this.answers.deleteAllAnswers(loginUser);
        deleteHistories.add(DeleteHistory.generateQuestionHistory(this.getId(), loginUser));

        return deleteHistories;
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
        return "Question [id=" + getId() + ", title=" + this.questionBody.getTitle() + ", contents=" + this.questionBody.getContents() + ", writer=" + this.writer + "]";
    }
}
