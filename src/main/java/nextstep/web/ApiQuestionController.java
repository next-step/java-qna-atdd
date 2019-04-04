package nextstep.web;


import java.net.URI;
import javax.validation.Valid;
import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    @Autowired
    private QnaService qnaService;

    @GetMapping("")
    public Iterable<Question> list(
        @PageableDefault(sort = {"id"}, direction = Direction.DESC, size = 20) Pageable pageable) {

        return qnaService.findAll(pageable);
    }

    @PostMapping("")
    public ResponseEntity create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question createdQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + createdQuestion.generateUrl()));
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Question show(@PathVariable long id) {
        return qnaService.findById(id);
    }

    @PutMapping("{id}")
    public Question update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Question updatedQuestion) {
        return qnaService.update(loginUser, id, updatedQuestion);
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@LoginUser User loginUser, @PathVariable long id)
        throws CannotDeleteException {

        qnaService.deleteQuestion(loginUser, id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
