package nextstep.service;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.domain.UserTest;
import org.hibernate.service.spi.InjectService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

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