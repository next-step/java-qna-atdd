package nextstep.service;

import com.fasterxml.jackson.databind.ser.Serializers;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import support.test.BaseTest;

public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void list_no_login() {
    }

    @Test
    public void list_login() {

    }

    // 로그인 유저가 질문
    // 게스트 유저가 질문 X
    // 질문 오너가 질문 수정
    // 질문 오너가 아닌 유저가 질문 수정 X
    // 질문 오너가 질문 삭제
    // 질문 오너가 아닌 유저가 질문 삭제 X
}