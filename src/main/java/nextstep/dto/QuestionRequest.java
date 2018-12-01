package nextstep.dto;

import javax.validation.constraints.Size;

public class QuestionRequest {

    @Size(min = 3, max = 100)
    private String title;

    @Size(min = 3)
    private String contents;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
