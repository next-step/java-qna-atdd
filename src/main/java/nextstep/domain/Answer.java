package nextstep.domain;

import lombok.*;
import nextstep.UnAuthenticationException;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
public class Answer extends AbstractEntity implements UrlGeneratable, DeleteHistoryGenerator {
    public static final int MIN_CONTENTS_LENGTH = 6;
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

    @Builder
    public Answer(User writer, Question question, String contents) {
        validate(contents);
        this.writer = writer;
        this.question = question;
        this.contents = contents;
    }

    public Answer(Long id, User writer, Question question, String contents) {
        super(id);
        this.writer = writer;
        this.question = question;
        this.contents = contents;
        this.deleted = false;
    }

    private void validate(String contents) {
        if(contents.length() < MIN_CONTENTS_LENGTH) {
            throw new IllegalArgumentException("무성의답변 안됩니다 ㅠㅠ");
        }
    }

    public Answer setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public Answer update(User loginUser, String contents) throws UnAuthenticationException {
        validateOwner(loginUser);
        validate(contents);
        this.contents = contents;
        return this;
    }

    public Answer delete(User loginUser) throws UnAuthenticationException {
        validateOwner(loginUser);
        this.deleted = true;
        return this;
    }

    private void validateOwner(User loginUser) throws UnAuthenticationException {
        if (isNotOwner(loginUser)) {
            throw new UnAuthenticationException("그대의 것이 아닌데?");
        }
    }

    public void toQuestion(Question question) {
        this.question = question;
    }

    public boolean isNotOwner(User loginUser) {
        return !writer.equalsNameAndEmail(loginUser);
    }

//    public create

    @Override
    public String generateUrl() {
        return String.format("%s/answers/%d", question.generateUrl(), getId());
    }

    @Override
    public DeleteHistory toDeleteHistory() {
        return DeleteHistory.builder()
                            .contentType(ContentType.ANSWER)
                            .contentId(this.getId())
                            .deletedBy(writer)
                            .build();
    }
}
