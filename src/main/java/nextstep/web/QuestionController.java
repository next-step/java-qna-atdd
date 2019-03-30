package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import nextstep.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import static nextstep.security.HttpSessionUtils.USER_SESSION_KEY;

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
    public String list(Model model, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC, size = 5) Pageable pageable) {
        Page<Question> questions = qnaService.findAll(pageable);
        model.addAttribute("questions", questions);
        model.addAttribute("number", questions.getNumber()+1);
        return "home";
    }

    @GetMapping("/{id}")
    public String detail(Model model, @PathVariable Long id, HttpSession httpSession) {
        Question question = qnaService.findById(id);
        addModifiableToModel(model, httpSession, question);
        model.addAttribute("question", question);

        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question target) {
        qnaService.update(loginUser, id, target);
        return "redirect:/questions/" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/questions";
    }

    private void addModifiableToModel(Model model, HttpSession httpSession, Question question) {
        User loginUser = (User)httpSession.getAttribute(USER_SESSION_KEY);

        if(question.matchedWriter(loginUser)) {
            model.addAttribute("isModifiable", true);
        }
    }
}
