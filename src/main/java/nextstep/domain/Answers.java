package nextstep.domain;

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

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }


    public void delete(User loginUser) {
        answers.stream().forEach(answer -> answer.delete(loginUser));
    }

    public boolean getSize() {
        return answers.size() != 0;
    }

    public boolean DeleteCheck(User loginUser) {
       return  answers.stream()
                .allMatch(answer -> answer.getWriter().equals(loginUser));
    }
}
