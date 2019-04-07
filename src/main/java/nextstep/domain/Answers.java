package nextstep.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Answers {
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    @JsonManagedReference
    private List<Answer> answers = new ArrayList<>();

    public void add(Answer answer) {
        this.answers.add(answer);
    }

    public List<DeleteHistory> deleteAllAnswers(User requestUser) {
        return this.answers.stream()
            .map(answer -> answer.delete(requestUser))
            .collect(Collectors.toList());
    }

    public boolean isAnswersSameOwner(User requestUser) {
        return this.answers.stream()
            .allMatch(answer -> answer.isOwner(requestUser));
    }
}
