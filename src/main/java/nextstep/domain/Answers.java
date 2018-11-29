package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.NotFoundException;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Answers {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public Answers() {
    }

    public void add(Answer answer) {
        answers.add(answer);
    }

    public Answer find(long answerId) {
        return this.answers.stream()
                .filter(answer -> answer.equalsId(answerId))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    public boolean hasAnswersOfOther(User loginUser) {
        this.answers.forEach(answer -> System.out.println(answer));

        return this.answers.stream()
                .anyMatch(answer -> !answer.isOwner(loginUser));
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        List<DeleteHistory> histories = new ArrayList<>();
        for (int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);
            histories.add(answer.delete(loginUser));
        }
        return histories;
    }
}
