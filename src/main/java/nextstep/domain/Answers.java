package nextstep.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import org.hibernate.annotations.Where;

@Embeddable
public class Answers {
	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
	@Where(clause = "deleted = false")
	@OrderBy("id ASC")
	private List<Answer> answers = new ArrayList<>();

	public Answers() {}

	public void add(Answer answer) {
		answers.add(answer);
	}

	public void addAll(List<Answer> answers) {
		this.answers.addAll(answers);
	}

	public boolean isSameOwnerOfAllAnswer(User writer) {
		return answers.stream()
				.allMatch(answer -> answer.isOwner(writer));
	}

	public List<DeleteHistory> deleteAll(User writer) {
		return answers.stream()
				.map(answer -> answer.delete(writer))
				.collect(Collectors.toList());
	}

	public List<Answer> getAnswers() {
		return answers;
	}
}
