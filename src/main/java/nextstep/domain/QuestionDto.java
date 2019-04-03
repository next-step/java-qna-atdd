package nextstep.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuestionDto {
    private String title;
    private String contents;
    private User writer;
    private List<Answer> answers = new ArrayList<>();
    private boolean deleted = false;

    public Question toEntity() {
        return  Question.builder()
                .title(title)
                .contents(contents)
                .writer(writer)
                .answers(answers)
                .build();
    }
}
