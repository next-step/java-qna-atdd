package nextstep.web;

import lombok.RequiredArgsConstructor;
import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class ApiAnswerController {

    private final QnaService qnaService;

    @GetMapping("/{id}")
    public Answer show(@PathVariable long id) {
        return qnaService.findAnswerById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Answer> update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Answer updatedAnswer) {
        Answer answer = qnaService.updateAnswer(loginUser, id, updatedAnswer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long id) {
        qnaService.deleteAnswer(loginUser, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
