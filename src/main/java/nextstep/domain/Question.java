package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity

public class Question extends AbstractEntity implements UrlGeneratable, Serializable {
	
	
	@Size(min = 3, max = 100)
	@Column(length = 100, nullable = false)
	private String title;
	
	@Size(min = 3)
	@Lob
	private String contents;
	
	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
	private User writer;

	@Embedded
	private Answers answers;
	
	private boolean deleted = false;
	
	public Question() {
	}
	
	public Question(String title, String contents) {
		this.title = title;
		this.contents = contents;
	}
	
	public Question(String title, String contents, User writer) {
		this(0L, title, contents, writer);
	}
	
	public Question(long id, String title, String contents, User writer) {
		super(id);
		this.title = title;
		this.contents = contents;
		this.writer = writer;
	}
	
	public Question(String title, String contents, User writer, Answers answers) {
		this(title, contents, writer);
		this.answers = answers;
	}

	public Question(long id, String title, String contents, User writer, Answers answers) {
		this(id, title, contents, writer);
		this.answers = answers;
	}

	public void update(Question target, User loginUser) throws CannotUpdateException {
		if (!this.writer.matchUser(loginUser)) {
			throw new CannotUpdateException("본인이 작성한 질문만 변경할 수 있습니다.");
		}
		this.answers = target.answers;
		this.contents = target.contents;
		this.title = target.title;
	}
	
	/**
	 * 삭제
	 * @param loginUser
	 * @return
	 */
	public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
		if (!isOwner(loginUser)) {
			throw new CannotDeleteException("본인이 작성한 질문만 삭제할 수 있습니다.");
		}
		
		this.deleted = true;
		
		List<DeleteHistory> deleteHistories = answers.deleteAnswers(loginUser);

		deleteHistories.add(new DeleteHistory(ContentType.QUESTION, getId(), loginUser, LocalDateTime.now()));
		
		return deleteHistories;
	}


	public String getTitle() {
		return title;
	}
	
	public Question setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public String getContents() {
		return contents;
	}
	
	public Question setContents(String contents) {
		this.contents = contents;
		return this;
	}
	
	public User getWriter() {
		return writer;
	}
	
	public void writeBy(User loginUser) {
		this.writer = loginUser;
	}
	
	public void addAnswer(Answer answer) {
		answer.toQuestion(this);
		answers.add(answer);
	}
	
	public boolean isOwner(User loginUser) {
		return writer.equals(loginUser);
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	
	@Override
	public String generateUrl() {
		return String.format("/questions/%d", getId());
	}
	
	@Override
	public String toString() {
		return "Question{" +
				"title='" + title + '\'' +
				", contents='" + contents + '\'' +
				", writer=" + writer +
				", answers=" + answers +
				", deleted=" + deleted +
				'}';
	}

	public Answers getAnswers() {
		return answers;
	}

	public void setAnswers(Answers answers) {
		this.answers = answers;
	}
}
