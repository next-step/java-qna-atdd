package nextstep.view;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.validation.constraints.Size;

public class QuestionUpdateView {

    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;


}
