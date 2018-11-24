package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QnaService qnaService;

    @GetMapping("/form")
    public String form() {
        return "/questions/form";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);

        return "redirect:/questions/";
    }

    //TODO 로그인한 사용자의 것만 수정 가능하게하기
    @GetMapping("/{{id}}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("user", qnaService.findById(id));
        return "/questions/form";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question updateQuestion) {
        qnaService.update(loginUser, id, updateQuestion);
        return "redirect:/questions";
    }


}
