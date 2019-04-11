package nextstep.domain;

import nextstep.UnAuthorizedException;
import nextstep.ForbiddenException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    @ManyToOne(optional = false)
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
        this(null, writer, questionBody);
    }

    public Question(Long id, User writer, QuestionBody questionBody) {
        super(id);

        if(writer == null) {
            throw new UnAuthorizedException();
        }
        this.writer = writer;
        this.questionBody = questionBody;
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
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

        getAnswers().forEach(a -> a.delete(writer));
    }

    public User getWriter() {
        return writer;
    }

    public QuestionBody getQuestionBody() {
        return questionBody;
    }

    public List<Answer> getAnswers() {
        return answers;
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
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
