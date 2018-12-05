package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Embedded
    private Answers answers = new Answers();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public Question(User user) {
        writeBy(user);
    }

    public Question(String title, String contents, User user) {
        this (title, contents);
        writeBy(user);
    }

    public Question(long id, String title, String contents, User writer) {
        super(id);
        this.title = title;
        this.contents = contents;
        this.writer = writer;
        this.deleted = false;
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

    public List<Answer> getAnswers() { return answers.getAnswers(); }

    public void addAnswer(Answer answer) {
        answers.addAnswer(answer);
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Question update(User loginUser, Question updatedQuestion) {
        if(!isOwner(loginUser)) {
            throw new UnAuthorizedException("질문을 수정할 권한이 없습니다.");
        }

        this.title = updatedQuestion.title;
        this.contents = updatedQuestion.contents;
        return this;
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        if(!isOwner(loginUser)) {
            throw new UnAuthorizedException("작성자만 삭제할 수 있습니다.");
        }

        if(!answers.isMatchedWriter(loginUser)) {
            throw new CannotDeleteException("질문자와 답변자가 같아야 삭제할 수 있습니다.");
        }

        this.deleted = true;
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        deleteHistories.add(generateQuestionDeleteHistory());
        deleteHistories.addAll(answers.deleteAllAnswer(loginUser));
        return deleteHistories;
    }

    private DeleteHistory generateQuestionDeleteHistory() {
        return new DeleteHistory(ContentType.QUESTION, getId(), writer, LocalDateTime.now());
    }

    public boolean equalsTitleAndContents(Question target) {
        if(Objects.isNull(target)) {
            return false;
        }

        return title.equals(target.title)
                && contents.equals(target.contents);
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
