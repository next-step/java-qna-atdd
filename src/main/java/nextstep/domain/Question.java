package nextstep.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
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

    @JsonBackReference
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this(title, contents, null);
    }

    public Question(String title, String contents, User writer) {
        this(0L, title, contents, writer);
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

    public boolean hasaAnswers() {
        return !answers.isEmpty();
    }

    public List<Answer> getAnswers() {
        return Collections.unmodifiableList(answers);
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public Answer addAnswer(Answer answer) {
        if (!answers.contains(answer)) {
            answer.toQuestion(this);
            answers.add(answer);
        }
        return answer;
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

    public void update(User loginUser, Question updatedQuestion) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        this.title = updatedQuestion.title;
        this.contents = updatedQuestion.contents;
    }

    public List<DeleteHistory> delete(User loginUser, DeletePolicy deletePolicy) throws CannotDeleteException {
        if (!deletePolicy.canPermission(this, loginUser)) {
            throw new CannotDeleteException("질문을 삭제할 수 없습니다.");
        }
        List<DeleteHistory> histories = new ArrayList<>();
        return delete(deleteAnswers(histories));
    }

    private List<DeleteHistory> delete(List<DeleteHistory> histories) {
        this.deleted = true;
        histories.add(DeleteHistory.fromQuestion(this));
        return histories;
    }

    private List<DeleteHistory> deleteAnswers(List<DeleteHistory> histories) throws CannotDeleteException {
        for (Answer answer : answers) {
            histories.add(answer.delete(writer));
        }
        return histories;
    }

    public boolean equalsTitleAndContentsAndWriter(Question otherQuestion) {
        if (Objects.isNull(otherQuestion)) {
            return false;
        }
        return title.equals(otherQuestion.title) 
                && contents.equals(otherQuestion.contents)
                && writer. equals(otherQuestion.writer);
    }
}