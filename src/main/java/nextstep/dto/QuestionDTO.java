package nextstep.dto;

/**
 * Created by hspark on 23/11/2018.
 */
public class QuestionDTO {
	private String title;

	private String contents;

	public String getTitle() {
		return title;
	}

	public String getContents() {
		return contents;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public QuestionDTO() {
	}

	public QuestionDTO(String title, String contents) {
		this.title = title;
		this.contents = contents;
	}
}
