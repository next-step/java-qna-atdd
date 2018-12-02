package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    @Embedded
    private QuestionBody body;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @Embedded
    private Answers answers;


    private boolean deleted = false;

    private Question() {
    }

    private Question(QuestionBody body) {
        this.body = body;
    }

    private Question(QuestionBody body, User login, List<Answer> answers) {
        this.body = body;
        this.writer = login;
        this.answers = new Answers(answers);
    }

    public static Question of(QuestionBody body) {
        return new Question(body);
    }

    public static Question ofList(QuestionBody body, User login, List<Answer> answers) {
        return new Question(body, login, answers);
    }

    public String getTitle() {
        return body.getTitle();
    }

    public String getContents() {
        return body.getContents();
    }

    public QuestionBody getBody() {
        return body;
    }

    public void setBody(QuestionBody body) {
        this.body = body;
    }

    public User getWriter() {
        return writer;
    }

    public void writeBy(User loginUser) {
        if (loginUser == null || loginUser.isGuestUser()) {
            throw new UnAuthorizedException();
        }
        this.writer = loginUser;
    }

    public void addAnswer(Answer answer) {
        answers.add(answer, this);
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
        return "Question{" +
                "body=" + body +
                ", writer=" + writer +
                ", deleted=" + deleted +
                '}';
    }

    public Question update(User loginUser, QuestionBody target) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        this.body = target;
        return this;
    }
    @Transactional
    public DeleteHistories delete(User loginUser) throws CannotDeleteException {
        if (!isOwner(loginUser)) {
            throw new CannotDeleteException("삭제권한없습니다.");
        }
        if(answers.isEmptyAnswer(loginUser)){
            throw new CannotDeleteException("답변이 남아있습니다.");
        }

        this.deleted = Boolean.TRUE;
        DeleteHistories deleteHistories = answers.deleteAll(loginUser);
        deleteHistories.deleteQuestion(this,loginUser);
        return deleteHistories;
    }
}
