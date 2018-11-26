package nextstep.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import nextstep.UnAuthorizedException;
import nextstep.dto.QuestionDTO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
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
    @JsonManagedReference
    private List<Answer> answers = new ArrayList<>();

    private boolean deleted = false;

    public Question() {
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

    public void update(User user, Question question) {
        if(!isOwner(user)){
            throw new UnAuthorizedException();
        }
        this.title = question.getTitle();
        this.contents = question.getContents();
    }

    public void delete(User requestUser){
        if(!this.isOwner(requestUser)){
            throw new UnAuthorizedException();
        }
        this.deleted = true;
    }

    public boolean equalsTitle(String title){
        return StringUtils.equals(title, this.title);
    }

    public boolean equalsContents(String contents){
        return StringUtils.equals(contents, this.contents);
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }

    public static Question of(QuestionDTO dto) {
        return new Question(dto.getTitle(), dto.getContents());
    }
}
