package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.dto.ListResponse;
import nextstep.security.LoginUser;
import nextstep.service.QnAService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@Controller
@RequestMapping("/api/questions")
public class ApiQuestionController {
    private final QnAService qnaService;

    public ApiQuestionController(QnAService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("")
    public ResponseEntity<ListResponse<Question>> list(Pageable pageable) {
        Page<Question> result = qnaService.findAll(pageable);

        return ResponseEntity.ok(new ListResponse<>(result));
    }

    @GetMapping("{id}")
    public ResponseEntity<Question> detail(@PathVariable Long id) {
        return ResponseEntity.ok(qnaService.findById(id));
    }

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @RequestBody Question request) {
        Question question = qnaService.create(loginUser, request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(question.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Question> update(@LoginUser User loginUser, @PathVariable Long id, @RequestBody Question beUpdatedQuestion) {
        Question question = qnaService.update(loginUser, id, beUpdatedQuestion);

        return ResponseEntity.ok(question);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable Long id) {
        qnaService.deleteQuestion(loginUser, id);

        return ResponseEntity.ok().build();
    }
}
