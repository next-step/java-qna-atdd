package nextstep.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.dto.QuestionDto;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    private static final String MSG_NOT_OWNER = "writer is not owner";

    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @Embedded
    private Answers answers = new Answers();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public void update(User writer, QuestionDto updatedQuestionDto) throws CannotUpdateException {
        isOwner(writer).orElseThrow(() -> new CannotUpdateException(MSG_NOT_OWNER));

        this.title = updatedQuestionDto.getTitle();
        this.contents = updatedQuestionDto.getContents();
    }

    public List<DeleteHistory> delete(User writer) throws CannotDeleteException {
        isOwner(writer).orElseThrow(() -> new CannotDeleteException(MSG_NOT_OWNER));

        if (!answers.allOwner(writer)) {
            throw new CannotDeleteException("Answers should contain only answer wrote by owner of question or empty.");
        }

        List<DeleteHistory> deleteHistories = deleteAnswers(writer);

        this.deleted = true;
        deleteHistories.add(createDeleteHistory());

        return deleteHistories;
    }

    private DeleteHistory createDeleteHistory() {
        return new DeleteHistory(ContentType.QUESTION, getId(), writer);
    }

    private List<DeleteHistory> deleteAnswers(User writer) throws CannotDeleteException {
        return answers.delete(writer);
    }

    public Optional<User> isOwner(User writer) {
        return Optional.of(writer)
                .filter(user -> this.writer.equals(writer));
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

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + ", deleted=" + deleted +"]";
    }

    @JsonIgnore
    public List<Answer> getAnswers() {
        return answers.getAnswers();
    }

    public int sizeAnswers() {
        return answers.size();
    }

    public boolean isDeletedWithAllAnswers() {
        return deleted && answers.allDeleted();
    }
}
