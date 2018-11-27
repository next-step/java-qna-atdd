package nextstep.dto;

/**
 * Created by hspark on 24/11/2018.
 */
public class AnswerDTO {
	private String contents;

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getContents() {
		return contents;
	}

	public AnswerDTO() {
	}

	public AnswerDTO(String contents) {
		this.contents = contents;
	}
}
