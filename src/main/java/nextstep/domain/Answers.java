package nextstep.domain;

import nextstep.CannotDeleteException;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Answers {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers;

    public Answers() {
        this.answers = new ArrayList<>();
    }
    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

    public void isAnswerExists(User loginUser) throws CannotDeleteException {
        for(Answer answer : this.answers) {
            answer.delete(loginUser);
        }
    }

    public void addToDeleteHistory(List<DeleteHistory> histories) {
        this.answers.stream()
                .map(answer -> new DeleteHistory(ContentType.ANSWER, answer.getId(), answer.getWriter(), LocalDateTime.now()))
                .forEach(histories::add);
    }

    public List<Answer> getAnswers() {
        return this.answers;
    }

    public int getSize () {
        return this.answers.size();
    }
}
