package nextstep.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.hibernate.annotations.Where;

@Embeddable
public class Answers {

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  @Where(clause = "deleted = false")
  @OrderBy("id ASC")
  private List<Answer> answers = new ArrayList<>();

  public void add(Answer answer) {
    answers.add(answer);
  }

  List<DeleteHistory> deleteAll(User loginUser) throws CannotDeleteException {
    List<DeleteHistory> deleteHistories;
    try {
      deleteHistories = this.answers.stream()
          .map(answer -> answer.delete(loginUser))
          .collect(Collectors.toList());
    } catch(UnAuthorizedException unAuthorizedException) {
      throw new CannotDeleteException("삭제가 불가능한 답변이 존재합니다.");
    }

    return deleteHistories;
  }

  public boolean isContain(long answerId) {
    return answers.stream()
        .anyMatch(answer -> answer.matchId(answerId));
  }
}
