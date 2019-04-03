package nextstep.domain;

import support.domain.AbstractEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Override
    public String toString() {
        return "DeleteHistory [id=" + id + ", contentType=" + contentType + ", contentId=" + contentId + ", deletedBy="
                + deletedBy + ", createDate=" + createDate + "]";
    }

    public boolean equalsEntity(AbstractEntity entity) {
        return equalsContentType(entity) && equalsContentId(entity) && equalsDeleter(entity);
    }

    private boolean equalsContentType(AbstractEntity entity) {
        if(entity instanceof Question) {
            return contentType.equals(contentType.QUESTION);
        }

        if(entity instanceof Answer) {
            return contentType.equals(contentType.ANSWER);
        }

        return false;
    }

    private boolean equalsContentId(AbstractEntity entity) {
        return contentId == entity.getId();
    }

    private boolean equalsDeleter(AbstractEntity entity) {
        if(entity instanceof Question) {
            return deletedBy.equals(((Question) entity).getWriter());
        }

        if(entity instanceof Answer) {
            return deletedBy.equals(((Answer) entity).getWriter());
        }

        return false;
    }
}
