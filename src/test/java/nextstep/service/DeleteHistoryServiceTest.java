package nextstep.service;

import com.google.common.collect.ImmutableList;
import nextstep.domain.DeleteHistory;
import nextstep.domain.DeleteHistoryRepository;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DeleteHistoryServiceTest extends BaseTest {

    private DeleteHistoryService deleteHistoryService;

    private final DeleteHistoryRepository deleteHistoryRepository = mock(DeleteHistoryRepository.class);

    @Before
    public void setup() {
        this.deleteHistoryService = new DeleteHistoryService(this.deleteHistoryRepository);
    }

    @Test
    public void deleteAll() {
        final List<DeleteHistory> deleteHistories = ImmutableList.of(new DeleteHistory(), new DeleteHistory(), new DeleteHistory());

        deleteHistoryService.saveAll(deleteHistories);

        verify(deleteHistoryRepository, times(deleteHistories.size())).save(any(DeleteHistory.class));
    }

}