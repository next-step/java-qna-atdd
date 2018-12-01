package nextstep.domain;

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

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }


    public List<DeleteHistory> delete(User loginUser) {
        return answers.stream().map(answer -> answer.delete(loginUser)).collect(Collectors.toList());
    }

    public boolean hasSize() {
        return answers.size() != 0;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public boolean DeleteCheck(User loginUser) {
       return  answers.stream()
                .allMatch(answer -> answer.getWriter().equals(loginUser));
    }
}