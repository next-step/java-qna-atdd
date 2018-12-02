package nextstep.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Answer extends AbstractEntity implements UrlGeneratable {
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_writer"))
    private User writer;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_to_question"))
    private Question question;

    @Size(min = 5)
    @Lob
    private String contents;

    private boolean deleted = false;

    public Answer() {
    }

    public Answer(User writer, String contents) {
        this.writer = writer;
        this.contents = contents;
    }

    public Answer(Long id, User writer, Question question, String contents) {
        super(id);
        this.writer = writer;
        this.question = question;
        this.contents = contents;
        this.deleted = false;
    }

    public static Answer of(User writer, String contents) {
        if (writer == null || writer.isGuestUser()) {
            throw new UnAuthorizedException();
        }
        return new Answer(writer, contents);
    }

    public static Answer ofQuestion(Long id, User writer, Question question, String contents) {
        if (writer == null || writer.isGuestUser()) {
            throw new UnAuthorizedException();
        }
        if (question == null) {
            throw new UnAuthorizedException();
        }
        return new Answer(id, writer, question, contents);
    }

    public Question getQuestion() {
        return question;
    }

    public String getContents() {
        return contents;
    }

    public Answer setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public void toQuestion(Question question) {
        this.question = question;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public String generateUrl() {
        return String.format("%s/answers/%d", question.generateUrl(), getId());
    }

    @Override
    public String toString() {
        return "Answer [id=" + getId() + ", writer=" + writer + ", contents=" + contents + "]";
    }

    public Answer update(User loginUser, String contents) {
        if (!writer.equals(loginUser)) {
            throw new UnAuthorizedException();
        }
        this.contents = contents;
        return this;
    }

    public DeleteHistory delete(User loginUser) throws CannotDeleteException {
        if (!isOwner(loginUser)) {
            throw new CannotDeleteException("삭제권한없습니다.");
        }
        this.deleted = true;
        return DeleteHistory.of(ContentType.ANSWER, getId(), loginUser);
    }
}
