package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
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

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }

    public void update(User loginUser, Question target) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        this.title = target.title;
        this.contents = target.contents;

    }

    public List<DeleteHistory> deleted(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        this.deleted = true;
        System.out.println(loginUser.getUserId());
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        System.out.println("this is success?");
        deleteHistories.add(new DeleteHistory(ContentType.QUESTION, this.getId(), this.writer , LocalDateTime.now()));
        System.out.println("history1 success");
        List<DeleteHistory> deleteHistories1 = this.answers.stream().map(e -> e.delete(loginUser)).collect(Collectors.toList());

        for(int i = 0 ; i < deleteHistories1.size() ; i++){
            System.out.println(deleteHistories1.get(i).toString());
        }
        System.out.println("answer history size is : " + deleteHistories1.size());

        deleteHistories.addAll(this.answers.stream().map(e -> e.delete(loginUser)).collect(Collectors.toList()));
        System.out.println("history2 success");
        return deleteHistories;
    }
    // private boolean matchUserId(User userId) {
    //    return this.writer.equals(userId);
    // }
}
