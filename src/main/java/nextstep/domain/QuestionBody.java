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

    public QuestionBody(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public QuestionBody() {
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public boolean equalsQuestionBody(Question body) {
        return this.getTitle().equals(body.getTitle()) && this.getContents().equals(body.getContents());
    }

    @Override
    public String toString() {
        return "QuestionBody{" +
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }

    public Question toQuestion() {
        return new Question(title, contents);
    }
}
