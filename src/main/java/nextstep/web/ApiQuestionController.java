package nextstep.web;


import java.net.URI;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

  @Resource(name = "qnaService")
  private QnaService qnaService;


  @PostMapping("")
  public ResponseEntity<Void> create(
      @LoginUser User loginUser, @Valid @RequestBody Question question) {

    Question savedQuestion = qnaService.create(loginUser, question);

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create("/api/questions/" + savedQuestion.getId()));
    return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
  }

  @GetMapping("")
  public List<Question> list(
      @RequestParam(required = false, defaultValue = "1") int page,
      @RequestParam(required = false, defaultValue = "20") int size) {

    return qnaService.findAll(PageRequest.of(page - 1, size));
  }

  @GetMapping("{id}")
  public Question show(@PathVariable long id) {

    return qnaService.findById(id)
        .orElseThrow(EntityNotFoundException::new);
  }

  @PutMapping("{id}")
  public Question update(
      @LoginUser User loginUser,
      @PathVariable long id,
      @Valid @RequestBody Question updatedQuestion) {

    return qnaService.update(loginUser, id, updatedQuestion);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(
      @LoginUser User loginUser,
      @PathVariable long id) throws CannotDeleteException {

    qnaService.deleteQuestion(loginUser, id);
    return ResponseEntity.ok(null);
  }
}
