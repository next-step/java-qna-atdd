package nextstep.web;

import java.net.URI;
import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class ApiAnswerController {

  @Resource(name = "qnaService")
  private QnaService qnaService;

  @PostMapping("{questionId}/answers")
  public ResponseEntity<Void> create(
      @LoginUser User loginUser,
      @PathVariable(value = "questionId") long questionId,
      @RequestBody String contents) {

    Answer savedAnswer = qnaService.addAnswer(loginUser, questionId, contents);

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create("/api" + savedAnswer.generateUrl()));
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @GetMapping("{questionId}/answers/{id}")
  public Answer show(
      @PathVariable(value = "questionId") long questionId,
      @PathVariable(value = "id") long id) {

    return qnaService.findAnswerById(questionId, id);
  }
}
