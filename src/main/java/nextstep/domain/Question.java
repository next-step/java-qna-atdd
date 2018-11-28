package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.NotFoundException;
import nextstep.UnAuthorizedException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static nextstep.CannotDeleteException.ALREADY_DELETED_EXCEPTION;
import static nextstep.CannotDeleteException.HAS_ANSWERS_OF_OTHER_EXCEPTION;

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

    @Override
    public Question setId(long id) {
        super.setId(id);
        return this;
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

    public boolean isDeleted() {
        return deleted;
    }

    public Question setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public User getWriter() {
        return writer;
    }

    public Question writeBy(User loginUser) {
        this.writer = loginUser;
        return this;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public Answer findAnswer(long answerId) {
        return answers.stream()
                .filter(answer -> answer.equalsId(answerId))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    public void update(User loginUser, Question target) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        this.title = target.title;
        this.contents = target.contents;
    }

    public void delete(User loginUser) throws CannotDeleteException {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        if(isDeleted()) {
            throw new CannotDeleteException(ALREADY_DELETED_EXCEPTION);
        }

        if(hasAnswersOfOther(loginUser)) {
            throw new CannotDeleteException(HAS_ANSWERS_OF_OTHER_EXCEPTION);
        }

        this.deleted = true;
        this.deleteAnswers(loginUser);
    }

    private void deleteAnswers(User loginUser) throws CannotDeleteException {
        for (int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);
            answer.delete(loginUser);
        }
    }

    public boolean hasAnswersOfOther(User loginUser) {
        return answers.stream()
                .anyMatch(answer -> !answer.isOwner(loginUser));
    }

    public boolean equalsTitleAndContents(Question target) {
        if (Objects.isNull(target)) {
            return false;
        }

        return title.equals(target.title) &&
                contents.equals(target.contents);
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
