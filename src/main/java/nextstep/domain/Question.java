package nextstep.domain;

import nextstep.web.exception.ForbiddenException;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @Embedded
    private QuestionBody questionBody;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    private boolean deleted = false;

    public Question() {
    }

    public Question(User writer, QuestionBody questionBody) {
        if(writer == null) {
            // todo 좀 더 추상화된 예외로 처리하고, exceptionHandler 에서 받아서 httpStatus로 내려줘야함
            throw new IllegalArgumentException();
        }
        this.writer = writer;
        this.questionBody = questionBody;
    }

    public void update(User writer, QuestionBody newQuestionBody) {
        if(!isOwner(writer)) {
            throw new ForbiddenException();
        }

        questionBody = newQuestionBody;
    }

    public void delete(User writer) {
        if(!isOwner(writer)) {
            throw new ForbiddenException();
        }

        deleted = true;
    }


    public User getWriter() {
        return writer;
    }

    public QuestionBody getQuestionBody() {
        return questionBody;
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
}
