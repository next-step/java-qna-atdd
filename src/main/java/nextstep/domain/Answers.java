package nextstep.domain;

import nextstep.CannotDeleteException;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Answers {

    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public void add(Answer answer) {
        answers.add(answer);
    }

    public int size() {
        return answers.size();
    }

    public boolean hasOtherWriter(User loginUser) {
        return answers.stream().anyMatch(a -> a.isNotOwner(loginUser));
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        if (hasOtherWriter(loginUser)) {
            throw new CannotDeleteException("다른 사람의 답변이 존재하는 질문");
        }

        return answers.stream()
                .map(answer -> answer.delete(loginUser))
                .collect(Collectors.toList());
    }

    public Answer getAnswer(long answerId) {
        return answers.stream()
                .filter(answer -> answer.hasId(answerId))
                .findAny()
                .orElseThrow(EntityNotFoundException::new);
    }
}
