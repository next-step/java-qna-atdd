package nextstep.web;

import nextstep.domain.dto.AnswerResponseDto;
import nextstep.domain.entity.Answer;
import nextstep.domain.entity.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User user, @PathVariable long questionId, @Valid  @RequestBody String contents) {
        Answer savedAnswer = qnaService.addAnswer(user, questionId, contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(savedAnswer.generateApiUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{answerId}")
    public AnswerResponseDto show(@PathVariable long questionId, @PathVariable long answerId) {
        return qnaService.showAnswer(questionId, answerId);
    }

    @DeleteMapping("{answerId}")
    public AnswerResponseDto delete(@LoginUser User loginUser, @PathVariable long answerId) {
        return qnaService.deleteAnswer(loginUser, answerId);
    }
}
