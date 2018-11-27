package nextstep.web;

import java.net.URI;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);
    
    @Resource(name = "qnaService")
    private QnaService qnaService;
    
    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question savedQuestion = qnaService.create(loginUser, question);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + savedQuestion.generateUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }
    
    @GetMapping
    public List<Question> list() {
        return (List<Question>) qnaService.findAll();
    }
    
    @GetMapping("/{id}")
    public Question show(@PathVariable long id) {
        return qnaService.findById(id);
    }
    
    @PutMapping("/{id}")
    public Question update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Question updatedQuestion) {
        return qnaService.update(loginUser, id, updatedQuestion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

}
