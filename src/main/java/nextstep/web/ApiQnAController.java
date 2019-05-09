package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.ForbiddenException;
import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.QuestionBody;
import nextstep.domain.User;
import nextstep.dto.ListResponse;
import nextstep.security.LoginUser;
import nextstep.service.QnAService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@Controller
@RequestMapping("/api/questions")
public class ApiQnAController {
    private static final String URL_PREFIX = "/api/";

    private final QnAService qnaService;

    public ApiQnAController(QnAService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("")
    public ResponseEntity<ListResponse<Question>> findQuestions() {
        List<Question> list = qnaService.findQuestions();

        return ResponseEntity.ok(new ListResponse<>(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> findQuestion(@PathVariable Long id) {
        return ResponseEntity.ok(qnaService.findQuestionById(id));
    }

    @PostMapping("")
    public ResponseEntity<Void> createQuestion(@LoginUser User loginUser, @RequestBody Question newQuestion) {
        Question question = qnaService.createQuestion(loginUser, newQuestion);

        return ResponseEntity.created(URI.create(URL_PREFIX + question.generateUrl())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@LoginUser User loginUser, @PathVariable Long id, @RequestBody Question updatedQuestion) {
        Question question = qnaService.updateQuestion(loginUser, id, updatedQuestion);

        return ResponseEntity.ok(question);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@LoginUser User loginUser, @PathVariable Long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);
        } catch (CannotDeleteException e) {
            throw new ForbiddenException();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{questionId}/answers")
    public ResponseEntity<Void> addAnswer(@LoginUser User loginUser, @PathVariable Long questionId, @RequestBody String contents) {
        Answer answer = qnaService.addAnswer(loginUser, questionId, contents);

        return ResponseEntity.created(URI.create(URL_PREFIX + answer.generateUrl())).build();
    }

    @GetMapping("/{questionId}/answers")
    public ResponseEntity<ListResponse<Answer>> findAnswers(@PathVariable Long questionId) {
        List<Answer> answers = qnaService.findAnswers(questionId);

        return ResponseEntity.ok(new ListResponse<>(answers));
    }

    @GetMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<Answer> findAnswer(@PathVariable Long questionId, @PathVariable Long answerId) {
        Answer answer = qnaService.findAnswer(answerId);

        return ResponseEntity.ok(answer);
    }

    @DeleteMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@LoginUser User loginUser, @PathVariable Long questionId, @PathVariable Long answerId) {
        qnaService.deleteAnswer(loginUser, answerId);

        return ResponseEntity.ok().build();
    }
}
