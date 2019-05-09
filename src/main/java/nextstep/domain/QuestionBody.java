package nextstep.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.validation.constraints.Size;

@Embeddable
public class QuestionBody {
    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;

    public QuestionBody() {
    }

    public QuestionBody(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public QuestionBody setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public QuestionBody setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public String getContents() {
        return contents;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }

        if(obj == this) {
            return true;
        }

        if(obj.getClass() == getClass()) {
            QuestionBody questionBody = (QuestionBody) obj;
            return title.equals(questionBody.title)
                && contents.equals(questionBody.contents);
        }

        return false;
    }
}
