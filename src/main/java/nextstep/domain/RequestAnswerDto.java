package nextstep.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RequestAnswerDto {
    private User writer;
    private String contents;
    private Question question;
    
    public Answer toEntity() {
        return Answer.builder().writer(writer).question(question).contents(contents).build();
    }
}
