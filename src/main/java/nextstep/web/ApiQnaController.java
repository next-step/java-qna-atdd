package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.dto.QuestionDTO;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RequestMapping("/api/questions")
@RestController
public class ApiQnaController {

    private QnaService qnaService;

    public ApiQnaController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @PostMapping("")
    public ResponseEntity<Void> questionCreate(@Valid @RequestBody Question question, @LoginUser User loginUser) {
        Question saveQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + saveQuestion.getId()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public Question questionUpdate(@PathVariable("id") long id, @Valid @RequestBody Question question, @LoginUser User loginUser) {
        return qnaService.update(loginUser, id, question);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> questionDelete(@PathVariable("id") long id, @LoginUser User loginUser) {
        qnaService.deleteQuestion(loginUser, id);

        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<QuestionDTO> showQuestion(@PathVariable long id) {
        QuestionDTO question = qnaService.findQuestionAndAnswerById(id);
        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<>(question, headers, HttpStatus.OK);
    }

    @PostMapping("{id}/answer/add")
    public ResponseEntity<Answer> answerAdd(@LoginUser User loginUser, @PathVariable long id, @RequestBody Answer answer) {
        Answer newAnswer = qnaService.addAnswer(loginUser, id, answer.getContents());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + id));
        return new ResponseEntity<>(newAnswer, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}/answer/delete")
    public ResponseEntity<Answer> answerDelete(@LoginUser User loginUser, @PathVariable long id, @RequestBody Answer answer) {
        qnaService.deleteAnswer(loginUser, answer.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + id));
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
