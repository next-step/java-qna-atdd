package nextstep.domain;

import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static nextstep.domain.ContentType.ANSWER;
import static support.util.QnaUtil.not;

@Entity
public class Answer extends AbstractEntity implements UrlGeneratable, OwnerCheckable {
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

    public User getWriter() {
        return writer;
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

    public boolean isParentDeletable() {
        // 1. 부모의 유저와 답변의 유저가 동일한지 여부를 체크한다.
        if (not(question.isOwner(writer))) {
            return false;
        }

        return true;
    }

    public DeleteHistory delete(User user) {
        this.deleted = true;
        return createDeleteHistory(user);
    }

    private DeleteHistory createDeleteHistory(User user) {
        return new DeleteHistory(ANSWER, getId(), user, LocalDateTime.now());
    }
}
