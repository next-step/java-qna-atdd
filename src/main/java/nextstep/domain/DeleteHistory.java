package nextstep.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

import static nextstep.domain.ContentType.ANSWER;
import static nextstep.domain.ContentType.QUESTION;

@Entity
public class DeleteHistory {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    private Long contentId;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_deletehistory_to_user"))
    private User deletedBy;

    private LocalDateTime createDate = LocalDateTime.now();

    public DeleteHistory() {
    }

    public DeleteHistory(ContentType contentType, Long contentId, User deletedBy, LocalDateTime createDate) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.deletedBy = deletedBy;
        this.createDate = createDate;
    }

    public static DeleteHistory from(Question question, User loginUser) {
        return new DeleteHistory(QUESTION, question.getId(), loginUser, LocalDateTime.now());
    }

    public static DeleteHistory from(Answer answer, User loginUser) {
        return new DeleteHistory(ANSWER, answer.getId(), loginUser, LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "DeleteHistory [id=" + id + ", contentType=" + contentType + ", contentId=" + contentId + ", deletedBy="
                + deletedBy + ", createDate=" + createDate + "]";
    }
}
