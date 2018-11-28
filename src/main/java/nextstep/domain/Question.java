package nextstep.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;
import nextstep.AlreadyDeletedException;
import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

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

    public void addAnswers(List<Answer> answers) {
    	this.answers.addAll(answers);
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void update(User loginUser, Question target) {
    	if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("작성자만 가능합니다.");
        }

        this.title = target.title;
        this.contents = target.contents;
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
    	if (isDeleted()) {
    	    throw new AlreadyDeletedException("삭제된 질문입니다.");
        }
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("작성자만 변경할 수 있습니다.");
        }
        if (!answers.isSameOwnerOfAllAnswer(loginUser)) {
            throw new CannotDeleteException("답변이 없거나, 질문자와 답변자가 같아야 삭제 가능합니다.");
        }

	    deleted = true;
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        deleteHistories.add(generateDeleteHistory());
        deleteHistories.addAll(answers.deleteAll(loginUser));
        return deleteHistories;
    }

    private DeleteHistory generateDeleteHistory() {
        return new DeleteHistory(ContentType.QUESTION, getId(), writer);
    }

    public boolean equalsTitleAndContents(Question question) {
        return this.title.equals(question.title) && this.contents.equals(question.contents);
    }

    @JsonIgnore
    public List<Answer> getAnswers() {
        return answers.getAnswers();
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
