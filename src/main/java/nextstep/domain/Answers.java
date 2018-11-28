package nextstep.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hspark on 28/11/2018.
 */
@Embeddable
public class Answers {
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    @JsonManagedReference
    private List<Answer> answers = new ArrayList<>();

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public List<DeleteHistory> deleteAll(User requestUser) {
        return this.answers.stream().map(answer -> answer.delete(requestUser)).collect(Collectors.toList());
    }

    public boolean isAllAnswerSameOwner(User requestUser) {
        return answers.stream().allMatch(answer -> answer.isOwner(requestUser));
    }
}
