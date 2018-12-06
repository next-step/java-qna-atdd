package nextstep.domain;

import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Answers {
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public void add(Answer answer) {
        answers.add(answer);
    }

    private boolean isAnswerExist() {
        return answers != null && answers.size() > 0;
    }

    public boolean isAllAnswerDeletable() {
        if (!isAnswerExist()) {
            return true;
        }

        return answers.stream().allMatch(Answer::isParentDeletable);
    }

    public List<DeleteHistory> deleteAll(User user) {
        return this.answers.stream().map(a -> a.delete(user)).collect(toList());
    }

    public List<Answer> getAnswers() {
        return answers;
    }
}
