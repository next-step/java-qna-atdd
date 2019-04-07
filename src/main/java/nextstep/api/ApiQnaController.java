package nextstep.api;

import lombok.RequiredArgsConstructor;
import nextstep.CannotDeleteException;
import nextstep.domain.*;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class ApiQnaController {
    private final QnaService qnaService;
     @GetMapping("/{id}")
     public Question getQuestion(@LoginUser User loginUser, @PathVariable Long id) throws Exception {
         return qnaService.findById(id).orElse(null);
     }

    @PostMapping
     public ResponseEntity<Void> createQuestion(@LoginUser User loginUser, @RequestBody RequestQuestionDto question) {
         Question savedQuestion = qnaService.create(loginUser, question.toEntity());
         return getVoidResponseEntity("/api/questions/" + savedQuestion.getId());
     }

     @PutMapping("/{id}")
     public Question updateQuestion(@LoginUser User loginUser, @PathVariable Long id, @RequestBody RequestQuestionDto question) throws Exception {
         return qnaService.update(loginUser, id, question.toEntity());
     }

    @DeleteMapping("/{id}")
    public void deleteQuestion(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
    }

    @GetMapping("/{id}/answers/{answerId}")
    public Answer getQuestion(@LoginUser User loginUser, @PathVariable Long id, @PathVariable Long answerId) throws Exception {
        return qnaService.findByAnswerId(answerId).orElse(null);
    }

    @PostMapping("/{id}/answers")
    public ResponseEntity<Void> createAnswer(@LoginUser User loginUser, @PathVariable Long id, @RequestBody String contents) {
        Answer savedAnser = qnaService.addAnswer(loginUser, id, contents);
        return getVoidResponseEntity("/api/questions/" + id + "/answers/" + savedAnser.getId());
    }

    @PutMapping("/{id}/answers/{answerId}")
    public Answer updateAnswer(@LoginUser User loginUser, @PathVariable Long id, @PathVariable Long answerId, @RequestBody String contents) throws Exception{
        Answer savedAnser = qnaService.updateAnswer(loginUser, answerId, contents);
        return savedAnser;
    }

    @DeleteMapping("/{id}/answers/{answerId}")
    public void deleteAnswer(@LoginUser User loginUser, @PathVariable Long answerId) throws Exception{
        qnaService.deleteAnswer(loginUser, answerId);
    }

    private ResponseEntity<Void> getVoidResponseEntity(String location) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(location));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }
}
