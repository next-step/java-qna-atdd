package nextstep.service;

import nextstep.domain.DeleteHistories;
import nextstep.domain.DeleteHistory;
import nextstep.domain.DeleteHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("deleteHistoryService")
public class DeleteHistoryService {
    @Resource(name = "deleteHistoryRepository")
    private DeleteHistoryRepository deleteHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAll(DeleteHistories deleteHistories) {
        deleteHistoryRepository.saveAll(deleteHistories.getDeleteHistories());
    }

    public void save(DeleteHistory deleteHistory) {
        deleteHistoryRepository.save(deleteHistory);
    }

}
