package nextstep.domain;

import nextstep.UnAuthenticationException;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

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

    //질문 데이터를 완전히 삭제하는 것이 아니라 데이터의 상태를 삭제 상태(deleted - boolean type)로 변경한다.
    //로그인 사용자와 질문한 사람이 같은 경우 삭제 가능하다.
    //답변이 없는 경우 삭제가 가능하다.
    //질문자와 답변 글의 모든 답변자 같은 경우 삭제가 가능하다.
    //질문을 삭제할 때 답변 또한 삭제해야 하며, 답변의 삭제 또한 삭제 상태(deleted)를 변경한다.
    //질문자와 답변자가 다른 경우 답변을 삭제할 수 없다.
    //질문과 답변 삭제 이력에 대한 정보를 DeleteHistory를 활용해 남긴다.
    public DeleteHistory delete(User loginUser) throws UnAuthenticationException {
        if (!isOwner(loginUser)) {
            throw new UnAuthenticationException();
        }
        this.deleted = true;
        return new DeleteHistory(ContentType.ANSWER, getId(), loginUser, LocalDateTime.now());
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
}
