package nextstep.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestionRequestDTO {
    private String title;
    private String contents;

    @Builder
    public QuestionRequestDTO(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}
