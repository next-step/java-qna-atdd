package nextstep.domain;

import nextstep.exception.CannotDeleteException;
import nextstep.exception.UnAuthorizedException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    @Embedded
    private QuestionBody questionBody;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    private boolean deleted = false;

    public Question() {
        this.questionBody = new QuestionBody();
    }

    public Question(Long id, String title, String contents, User writer) {
        super(id);
        this.questionBody = new QuestionBody(title, contents);
        this.writer = writer;
    }

    public Question(String title, String contents) {
        this.questionBody = new QuestionBody(title, contents);
    }

    public QuestionBody getQuestionBody() {
        return this.questionBody;
    }

    public void setQuestionBody(QuestionBody questionBody) {
        this.questionBody = questionBody;
    }

    public String getTitle() {
        return this.questionBody.getTitle();
    }

    public String getContents() {
        return this.questionBody.getContents();
    }

    public User getWriter() {
        return writer;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public Answer addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
        return answer;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean hasAnswer(Answer answer) {
        return answers.stream().anyMatch(savedAnswer -> savedAnswer.equals(answer));
    }

    public Question update(User loginUser, QuestionBody updatedQuestion) throws UnAuthorizedException {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("작성자만 질문 수정이 가능합니다.");
        }
        this.questionBody = updatedQuestion;
        return this;
    }

    public boolean equalsQuestionBody(QuestionBody target) {
        if (Objects.isNull(target)) {
            return false;
        }
        return this.questionBody.equals(target);
    }

    private void isAvailableDelete(User loginUser) throws CannotDeleteException {
        if (!isOwner(loginUser)) {
            throw new CannotDeleteException("작성자만 질문 삭제가 가능합니다.");
        }
        if (isDeleted()) {
            throw new CannotDeleteException("이미 삭제된 글입니다.");
        }
    }

    private void deleteAnswers(User loginUser) throws CannotDeleteException {
        for (Answer answer : answers) {
            answer.delete(loginUser);
        }
    }

    private List<DeleteHistory> createDeleteHistory() {
        List<DeleteHistory> histories = answers.stream()
                .map(answer -> new DeleteHistory(ContentType.ANSWER, answer.getId(), answer.getWriter()))
                .collect(Collectors.toList());

        histories.add(new DeleteHistory(ContentType.QUESTION, this.getId(), this.getWriter()));
        return histories;
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        isAvailableDelete(loginUser);
        deleteAnswers(loginUser);
        deleted = true;
        return createDeleteHistory();
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", " + questionBody + ", " + ", writer=" + writer + "]";
    }
}
