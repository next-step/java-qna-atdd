package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public Question(long id, String title, String contents, User writer, boolean deleted) {
        super(id);
        this.title = title;
        this.contents = contents;
        this.writer = writer;
        this.deleted = deleted;
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

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void update(User user, Question updatedQuestion) {
        if (!isOwner(user)) {
            throw new UnAuthorizedException("The owner doesn't match");
        }

        if (isDeleted()) {
            throw new IllegalStateException("It's deleted question");
        }

        updateQuestion(updatedQuestion);
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("The owner doesn't match");
        }

        if (this.isDeleted()) {
            throw new CannotDeleteException("This question has already deleted");
        }

        if (this.hasUsedAnswerOfOtherUser()) {
            throw new CannotDeleteException("There's other user's question.");
        }

       return deleteAnswersAndQuestion();
    }

    public String generateRestUrl() {
        return String.format("/api/questions/%d", getId());
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }

    List<Answer> getUsedAnswers() {
        return this.answers.stream()
                .filter(answer -> !answer.isDeleted())
                .collect(Collectors.toList());
    }

    private void updateQuestion(Question updatedQuestion) {
        this.title = updatedQuestion.title;
        this.contents = updatedQuestion.contents;
    }

    private boolean hasUsedAnswerOfOtherUser() {
        long countOfAnswersOfOtherUser = getUsedAnswers().stream()
                .filter(answer -> !answer.isOwner(this.writer))
                .count();

        return 0 < countOfAnswersOfOtherUser;
    }

    private List<DeleteHistory> deleteAnswersAndQuestion() throws CannotDeleteException {
        List<DeleteHistory> deleteHistories = deleteAnswers();

        DeleteHistory questionDeleteHistory = deleteQuestion();
        deleteHistories.add(questionDeleteHistory);

        return deleteHistories;
    }

    private List<DeleteHistory> deleteAnswers() throws CannotDeleteException {
        List<Answer> usedAnswers = getUsedAnswers();
        List<DeleteHistory> answerDeleteHistories = new ArrayList(usedAnswers.size());

        for (Answer answer : usedAnswers) {
            DeleteHistory answerDeleteHistory = answer.delete(this.writer);
            answerDeleteHistories.add(answerDeleteHistory);
        }

        return answerDeleteHistories;
    }

    private DeleteHistory deleteQuestion() {
        this.deleted = true;

        return new DeleteHistory(ContentType.QUESTION, getId(), this.writer);
    }

}
