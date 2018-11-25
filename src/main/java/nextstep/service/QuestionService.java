package nextstep.service;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("questionService")
public class QuestionService {
    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    public List<Question> findAll() {
        return questionRepository.findAll();
    }
}
