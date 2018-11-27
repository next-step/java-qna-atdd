package nextstep.domain;

import com.sun.xml.internal.ws.developer.Serialization;
import nextstep.CannotUpdateException;
import nextstep.UnAuthenticationException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity

public class Question extends AbstractEntity implements UrlGeneratable, Serializable {


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

    public Question(String title, String contents, User writer) {
        this(0L, title, contents, writer);
    }

    public Question(long id, String title, String contents, User writer) {
        super(id);
        this.title = title;
        this.contents = contents;
        this.writer =  writer;
    }

    public Question(String title, String contents, User writer, List<Answer> answers) {
        this.title = title;
        this.contents = contents;
        this.writer =  writer;
        this.answers = answers;
    }

    public void update(Question target, User loginUser) throws CannotUpdateException {
        if (!this.writer.matchUser(loginUser)) {
            throw new CannotUpdateException("본인이 작성한 질문만 변경할 수 있습니다.");
        }
        this.answers = target.answers;
        this.contents = target.contents;
        this.title = target.title;
    }

    public void deleteAnswer(Answer answer) {
        answers.removeIf(a-> {
            return a.equals(answer);
        });
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
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", writer=" + writer +
                ", answers=" + answers +
                ", deleted=" + deleted +
                '}';
    }

    public List<Answer> getAnswers() {
        return answers;
    }
}
