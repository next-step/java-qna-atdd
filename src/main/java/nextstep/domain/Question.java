package nextstep.domain;

import lombok.Getter;
import lombok.Setter;
import nextstep.UnAuthenticationException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

    // validator에서 처리해 주는 것 같은데 굳이 또 여기서 할 필요 있을까요..?
    public Question(String title, String contents) {
        validate(title, contents);
        setTitle(title);
        setContents(contents);
    }

    public Question(String title, String contents, Long id) {
        this(title, contents);
        super.setId(id);
    }

    private void validate(String title, String contents) {
        if(title.length() < 3 | title.length() > 100) {
             throw new IllegalArgumentException("제목은 3 ~ 100 자 내에서 가능합니다");
        }
        if(contents.length() < 3) {
            throw new IllegalArgumentException("내용이 빈약하네요. 좀 더 써보세요");
        }
    }

    public Question setTitle(String title) {
        this.title = title;
        return this;
    }

    public Question setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    //FIXED : 유저 업데이트 테스트 중인데... 분명히 데이터가 다른데
    //같다고 나옵니다.. 왜그러는 거죠;;;
    public boolean isOwner(User loginUser) {
        System.out.println("넌 이게 같냐? " + writer) ;
        System.out.println("유저는 ? " + loginUser);
        System.out.println("가아? " + writer.equals(loginUser));
        return writer.equals(loginUser);
    }

    public Question update(User loginUser, Question question) throws UnAuthenticationException {
        if(!isOwner(loginUser)) {
            System.out.println("여기 안옴?");
            throw new UnAuthenticationException("그대의 것이 아닌데?");
        }
        validate(question.title, question.contents);
        setTitle(question.title);
        setContents(question.contents);
        return this;
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
