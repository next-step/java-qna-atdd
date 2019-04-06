package nextstep.web;

import lombok.RequiredArgsConstructor;
import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
@RequiredArgsConstructor
public class ApiAnswerController {

    private final QnaService qnaService;

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionId, @RequestBody String contents) {
        Answer createdAnswer = qnaService.createAnswer(loginUser, questionId, contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + createdAnswer.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public Answer show(@PathVariable long questionId, @PathVariable long id) {
        return qnaService.findAnswerById(questionId, id);
    }

    @PutMapping("/{id}")
    public Answer update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Answer updatedAnswer) {
        return qnaService.updateAnswer(loginUser, id, updatedAnswer);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long id) {
        qnaService.deleteAnswer(loginUser, questionId, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
