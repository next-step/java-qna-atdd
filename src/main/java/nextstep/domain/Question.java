package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import nextstep.view.QuestionView;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
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
    private Answers answers;

    private boolean deleted = false;

    public Question() {
    }

    public Question(Long id ,String title, String contents, User writer, Answers answers) {
        super(id);
        this.title = title;
        this.contents = contents;
        this.writer = writer;
        this.answers = answers;
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
        this.answers = new Answers();
    }

    public Question(long id, String title, String contents, User writer) {
        this(id, title, contents, writer, new Answers());
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question{" +
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", writer=" + writer +
                ", answers=" + answers +
                ", deleted=" + deleted +
                '}';
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public User getWriter() {
        return writer;
    }

    public String getContents() {
        return contents;
    }

    public String getTitle() {
        return title;
    }

    public Answers getAnswers() {
        return answers;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.addAnswer(answer);
    }

    public Question update(Question updatedQuestion) {
        if(!isOwner(updatedQuestion.getWriter())){
            throw new UnAuthorizedException();
        }

        this.title = updatedQuestion.title;
        this.contents = updatedQuestion.contents;

        return this;
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        if(isDeleted()){
            throw new CannotDeleteException("삭제된 질문입니다.");
        }
        if(!isOwner(loginUser)){
            throw new UnAuthorizedException("작성자가 아닙니다.");
        }
        deleted = true;
        List<DeleteHistory> deleteHistories = answers.delete(loginUser);
        deleteHistories.add(new DeleteHistory(ContentType.ANSWER, getId(), loginUser, LocalDateTime.now()));

        return deleteHistories;
    }

}
