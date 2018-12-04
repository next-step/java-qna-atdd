package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static nextstep.domain.ContentType.QUESTION;
import static support.util.QnaUtil.not;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable, OwnerCheckable {
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
        this(title, contents);
        this.writeBy(writer);
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
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }

    public void update(User loginUser, Question updatedQuestion) {
        if (not(isOwner(loginUser))) {
            throw new UnAuthorizedException("사용자가 일치하지 않음");
        }

        this.title = updatedQuestion.getTitle();
        this.contents = updatedQuestion.getContents();
    }

    public List<DeleteHistory> delete(User loginUser) {
        checkDeletable(loginUser);
        return deleteInternal(loginUser);
    }

    private void checkDeletable(User loginUser) {
        if (not(isOwner(loginUser))) {
            throw new UnAuthorizedException("사용자가 일치하지 않음");
        }

        if (this.deleted) {
            throw new IllegalStateException("이미 삭제됨");
        }

        // 답변이 존재하는 경우, 삭제여부 확인할 것
        if (isAnswersExist() && not(isAllAnswerDeletable())) {
            throw new CannotDeleteException("다른 유저가 작성한 답변이 존재함");
        }
    }

    private List<DeleteHistory> deleteInternal(User user) {
        this.deleted = true;

        List<DeleteHistory> histories = new ArrayList<>();

        histories.add(createDeleteHistory(user));

        List<DeleteHistory> answerDeleteHistories = this.answers.stream().map(a -> a.delete(user)).collect(toList());
        histories.addAll(answerDeleteHistories);

        return histories;
    }

    private DeleteHistory createDeleteHistory(User user) {
        return new DeleteHistory(QUESTION, getId(), user, LocalDateTime.now());
    }

    private boolean isAnswersExist() {
        return answers != null && answers.size() > 0;
    }

    private boolean isAllAnswerDeletable() {
        return answers.stream().allMatch(Answer::isParentDeletable);
    }
}
