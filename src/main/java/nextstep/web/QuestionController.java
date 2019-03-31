package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question target) {
        qnaService.create(loginUser, target);
        return "redirect:/questions";
    }

    @GetMapping("")
    public String list(Model model, HttpSession httpSession, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC, size = 5) Pageable pageable) {
        Page<Question> questions = qnaService.findAll(pageable);
        model.addAttribute("questions", questions);
        model.addAttribute("number", questions.getNumber()+1);
        return "home";
    }

    @GetMapping("/{id}")
    public String detail(Model model, @PathVariable Long id) {
        try {
            Question question = qnaService.findById(id);
            model.addAttribute("question", question);
        } catch( EntityNotFoundException e) {
            e.printStackTrace();
            return "redirect:/questions";
        }

        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question target) {
        try {
            qnaService.update(loginUser, id, target);
        } catch (UnAuthorizedException e) {
            return "redirect:/login";
        }
        return "redirect:/questions/" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        try {
            qnaService.deleteQuestion(loginUser, id);
        } catch (UnAuthorizedException e) {
            return "redirect:/login";
        }

        return "redirect:/questions";
    }
}
