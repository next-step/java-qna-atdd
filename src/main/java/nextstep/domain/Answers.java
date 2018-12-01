package nextstep.domain;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Answers implements Serializable {

    @JsonDeserialize(contentAs = Answer.class)
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public void addAnswer(Question question, Answer answer) {
        if (this.answers.contains(answer)) {
            return;
        }
        answer.toQuestion(question);
        answers.add(answer);
    }

    public List<Answer> getAnswers() {
        return this.answers;
    }

    public int size() {
        return this.answers.size();
    }

    public List<DeleteHistory> deleteAll(User loginUser, Long questionId) {

        return this.answers.stream().map(answer -> answer.delete(loginUser, questionId)).collect(Collectors.toList());
    }

    public boolean contains(Answer answer) {
        return this.answers.contains(answer);
    }

    public void addAll(Collection<Answer> answers, Question question) {
        for (Answer answer : answers) {
            this.addAnswer(question, answer);
        }
    }

    @JsonIgnore
    public boolean isAllDeleted() {
        return this.answers.stream().allMatch(Answer::isDeleted);
    }

    public Answer get(int index) {
        return this.answers.get(index);
    }

    @Override
    public String toString() {
        return "Answers{" +
                "answers=" + answers +
                '}';
    }
}
