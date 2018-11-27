package nextstep.service;

import nextstep.NotFoundException;
import nextstep.UnAuthenticationException;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.domain.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test(expected = NotFoundException.class)
    public void findOne() {
        when(questionRepository.findById(0L)).thenReturn(Optional.empty());

        qnaService.findOne(0);
    }

//
//    @Test
//    public void login_success() throws Exception {
//        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
//        when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));
//
//        User loginUser = userService.login(user.getUserId(), user.getPassword());
//        softly.assertThat(loginUser).isEqualTo(user);
//    }
//
//    @Test(expected = UnAuthenticationException.class)
//    public void login_failed_when_user_not_found() throws Exception {
//        when(userRepository.findByUserId("sanjigi")).thenReturn(Optional.empty());
//
//        userService.login("sanjigi", "password");
//    }
//
//    @Test(expected = UnAuthenticationException.class)
//    public void login_failed_when_mismatch_password() throws Exception {
//        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
//        when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));
//
//        userService.login(user.getUserId(), user.getPassword() + "2");
//    }
}
