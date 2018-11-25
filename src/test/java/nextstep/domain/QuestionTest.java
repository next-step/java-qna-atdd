package nextstep.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class QuestionTest {

    // 제목 수정
    // 내용 수정
    // 질문자 설정
    // 질문 추가
    // 본인 질문인지 체크

    @Test
    public void update_title() {
        String otherTitle = "Hi 뜻은?";
        Question question = new Question("하이 뜻은?", "한국어 뜻은?");
        Question updatedQuestion = new Question(otherTitle, "한국어 뜻은?");
        question.setTitle(otherTitle);
        assertThat(question.getTitle()).isEqualTo(updatedQuestion.getTitle());
    }

    @Test
    public void update_contents() {
        String otherContent = "아무거나 내용";
        Question question = new Question("하이 뜻은?", "한국어 뜻은?");
        Question otherQuestion = new Question("하이 뜻은", otherContent);
        question.setContents(otherContent);
        assertThat(question.getContents()).isEqualTo(otherQuestion.getContents());
    }

    @Test
    public void add_answer() {
    }

}