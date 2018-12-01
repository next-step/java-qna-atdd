package nextstep.domain;

import nextstep.dto.QuestionRequest;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.validation.constraints.Size;
import java.util.Objects;

@Embeddable
public class QuestionPost {

    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;

    public QuestionPost() {
    }

    public QuestionPost(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public static QuestionPost from(QuestionRequest questionRequest) {
        return new QuestionPost(questionRequest.getTitle(), questionRequest.getContents());
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionPost that = (QuestionPost) o;
        return title.equals(that.title) &&
                contents.equals(that.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, contents);
    }

    @Override
    public String toString() {
        return "QuestionPost{" +
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }
}
