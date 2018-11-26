package nextstep.domain;

import nextstep.UnAuthorizedException;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;

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

    public static Answer of(User writer, String contents){
        if(writer == null || writer.isGuestUser()){
            throw new UnAuthorizedException();
        }
        return new Answer(writer, contents);
    }
    public static Answer ofQuestion(Long id, User writer, Question question, String contents){
        if(writer == null || writer.isGuestUser()){
            throw new UnAuthorizedException();
        }
        if(question == null){
            throw new UnAuthorizedException();
        }
        return new Answer(id, writer, question, contents);
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

    public void update(Answer updateAnswer) {
        if(!updateAnswer.isOwner(this.writer)){
            throw new UnAuthorizedException();
        }
        this.contents = updateAnswer.getContents();
    }

    public void delete(User loginUser) {
        if(!writer.equals(loginUser)){
            throw new UnAuthorizedException();
        }
        this.deleted = true;
    }
}
