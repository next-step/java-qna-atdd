package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/questions")
public class QuestionController {

    private static final String QUESTION_LIST_PATE = "redirect:/questions";

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping
    public String list(final Model model,
                       final Pageable pageable) {
        model.addAttribute("question", qnaService.findAll(pageable));
        return "/qna/show";
    }

    @SuppressWarnings("unused")
    @GetMapping("/form")
    public String updateForm(@LoginUser final User loginUser) {
        return "/qna/form";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser final User loginUser,
                             final Model model,
                             @PathVariable final long id) {
        model.addAttribute("question", qnaService.findByUserAndId(loginUser, id));
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser final User loginUser,
                         @PathVariable final long id,
                         final Question question) {
        qnaService.update(loginUser, id, question);
        return QUESTION_LIST_PATE;
    }

    @PostMapping
    public String create(@LoginUser final User loginUser,
                         final Question question) {
        qnaService.create(loginUser, question);
        return QUESTION_LIST_PATE;
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser final User loginUser,
                         @PathVariable final long id) throws CannotDeleteException {
        qnaService.delete(loginUser, id);
        return QUESTION_LIST_PATE;
    }

}
