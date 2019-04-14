package nextstep.service;

import nextstep.domain.entity.DeleteHistory;
import nextstep.domain.entity.Question;
import nextstep.domain.repository.DeleteHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("deleteHistoryService")
public class DeleteHistoryService {
    @Resource(name = "deleteHistoryRepository")
    private DeleteHistoryRepository deleteHistoryRepository;

    @Transactional
    public void saveAll(List<DeleteHistory> deleteHistories) {
        for (DeleteHistory deleteHistory : deleteHistories) {
            deleteHistoryRepository.save(deleteHistory);
        }
    }

    @Transactional
    public void saveAll(Question question) {
        List<DeleteHistory> deleteHistories = addDeleteHistories(question);

        for (DeleteHistory deleteHistory : deleteHistories) {
            deleteHistoryRepository.save(deleteHistory);
        }
    }

    private List<DeleteHistory> addDeleteHistories(Question question) {
        return DeleteHistory.toDeleteHistories(question);
    }
}
