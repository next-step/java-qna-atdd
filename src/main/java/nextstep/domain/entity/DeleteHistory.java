package nextstep.domain.entity;

import nextstep.domain.ContentType;
import org.hibernate.sql.Delete;
import support.domain.AbstractEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DeleteHistory extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    private Long contentId;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_deletehistory_to_user"))
    private User deletedBy;

    public DeleteHistory() {
    }

    public DeleteHistory(ContentType contentType, Long contentId, User deletedBy) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.deletedBy = deletedBy;
    }

    public DeleteHistory(Long id, ContentType contentType, Long contentId, User deletedBy) {
        super(id);
        this.contentType = contentType;
        this.contentId = contentId;
        this.deletedBy = deletedBy;
    }

    public static List<DeleteHistory> toDeleteHistories(Question question) {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        addDeletedQuestion(question, deleteHistories);
        addDeletedAnswer(question, deleteHistories);
        return deleteHistories;
    }

    private static void addDeletedAnswer(Question question, List<DeleteHistory> deleteHistories) {
        for (Answer answer : question.getAnswers()) {
            deleteHistories.add(new DeleteHistory(ContentType.ANSWER, answer.getId(), answer.getWriter()));
        }
    }

    private static void addDeletedQuestion(Question question, List<DeleteHistory> deleteHistories) {
        deleteHistories.add(new DeleteHistory(ContentType.QUESTION, question.getId(), question.getWriter()));
    }

    public ContentType getContentType() {
        return contentType;
    }

    public Long getContentId() {
        return contentId;
    }

    public User getDeletedBy() {
        return deletedBy;
    }

    @Override
    public String toString() {
        return "DeleteHistory [id=" + super.getId() + ", contentType=" + contentType + ", contentId=" + contentId + ", deletedBy="
                + deletedBy + ", createDate=" + super.getFormattedCreateDate() + "]";
    }
}
