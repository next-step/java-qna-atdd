package nextstep.domain;

import lombok.*;
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
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(exclude = {"answers"})
public class Question extends AbstractEntity implements UrlGeneratable {
    public static final int MIN_DATA_LENGTH = 3;
    public static final int MAX_TITLE_LENGTH = 100;
    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;

    // FIXED : (cascade={CascadeType.ALL}) 얘와 관련이 되어 보입니다?
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    private boolean deleted = false;

    @Builder
    private Question(String title, String contents) {
        validate(title, contents);
        this.title = title;
        this.contents = contents;
    }

    private Question(String title, String contents, Long id) {
        this(title, contents);
        super.setId(id);
    }

    private void validate(String title, String contents) {
        validateTitle(title);
        validateContents(contents);
    }

    private void validateTitle(String title) {
        if(title.length() < MIN_DATA_LENGTH | title.length() > MAX_TITLE_LENGTH) {
             throw new IllegalArgumentException("제목은 3 ~ 100 자 내에서 가능합니다");
        }
    }

    private void validateContents(String contents) {
        if(contents.length() < MIN_DATA_LENGTH) {
            throw new IllegalArgumentException("내용이 빈약하네요. 좀 더 써보세요");
        }
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }
    
    public boolean isNotOwner(User loginUser) {
        return !writer.equalsNameAndEmail(loginUser);
    }

    public Question update(User loginUser, Question question) throws UnAuthenticationException {
        if(isNotOwner(loginUser)) {
            throw new UnAuthenticationException("그대의 것이 아닌데?");
        }
        validate(question.title, question.contents);
        this.title = question.title;
        this.contents = question.contents;
        return this;
    }

    public void deleteQuestion() {
        this.deleted = true;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }
}
