package nextstep.domain;

import nextstep.CannotDeleteException;
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
    private List<Answer> answers = new ArrayList<>();

    public Answers(List<Answer> answers) {
        this.answers = answers;
    }

    public Answers() {
    }

    public List<DeleteHistory> delete(User loginUser) {
        return answers.stream()
                .map(answer -> answer.tryDelete(loginUser))
                .collect(Collectors.toList());
    }

    public void add(Answer answer) {
        answers.add(answer);
    }

    public boolean containsAnswer(long id) {
        return answers.stream()
                .anyMatch(answer -> answer.getId() == id);
    }

    public Answer get(int index) {
        return answers.get(index);
    }
}
