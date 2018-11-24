package nextstep.service;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import org.hibernate.service.spi.InjectService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {


    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void update_equal_writer() {

    }

    @Test
    public void update_not_equal_writer() {

    }

    @Test
    public void deleteQuestion() {
    }

    @Test
    public void addAnswer() {
    }

    @Test
    public void deleteAnswer() {
    }
}