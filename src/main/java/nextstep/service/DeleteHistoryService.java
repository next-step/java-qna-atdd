package nextstep.service;

import nextstep.domain.DeleteHistory;
import nextstep.domain.DeleteHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("deleteHistoryService")
public class DeleteHistoryService {
    @Resource(name = "deleteHistoryRepository")
    private DeleteHistoryRepository deleteHistoryRepository;

    @Transactional
    public void save(DeleteHistory deleteHistory) {
        deleteHistoryRepository.save(deleteHistory);
    }

    @Transactional
    public void saveAll(List<DeleteHistory> deleteHistories) {
        for (DeleteHistory deleteHistory : deleteHistories) {
            deleteHistoryRepository.save(deleteHistory);
        }
    }
}
