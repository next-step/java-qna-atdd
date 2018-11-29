package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/answers")
public class ApiAnswerController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PutMapping("/{id}")
    public Answer update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Answer updateAnswer) {
        return qnaService.updateAnswer(loginUser, id, updateAnswer);
    }

    @GetMapping("/{id}")
    public Answer showAnswer(@PathVariable long id) {
        return qnaService.findAnswerById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    @DeleteMapping("/{id}")
    public void delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteAnswer(loginUser, id);
    }
}
