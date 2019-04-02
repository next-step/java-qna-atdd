package nextstep.domain;

import nextstep.UnAuthenticationException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

        //질문 데이터를 완전히 삭제하는 것이 아니라 데이터의 상태를 삭제 상태(deleted - boolean type)로 변경한다.
        //로그인 사용자와 질문한 사람이 같은 경우 삭제 가능하다.
        //답변이 없는 경우 삭제가 가능하다.
        //질문자와 답변 글의 모든 답변자 같은 경우 삭제가 가능하다.
        //질문을 삭제할 때 답변 또한 삭제해야 하며, 답변의 삭제 또한 삭제 상태(deleted)를 변경한다.
        //질문자와 답변자가 다른 경우 답변을 삭제할 수 없다.
        //질문과 답변 삭제 이력에 대한 정보를 DeleteHistory를 활용해 남긴다.
    public List<DeleteHistory> delete(User loginUser) throws UnAuthenticationException {
        if (!isOwner(loginUser)) {
            throw new UnAuthenticationException();
        }
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        for (Answer answer : answers) {
            deleteHistories.add(answer.delete(loginUser));
        }
        this.deleted = true;
        deleteHistories.add(new DeleteHistory(ContentType.QUESTION, getId(), loginUser, LocalDateTime.now()));
        return deleteHistories;
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

    public void update(User loginUser, Question updatedQuestion) throws UnAuthenticationException {
        if (!isOwner(loginUser)) {
            throw new UnAuthenticationException();
        }
        this.title = updatedQuestion.title;
        this.contents = updatedQuestion.contents;
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
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }
}
