package nextstep.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeleteHistoryRepository extends CrudRepository<DeleteHistory, Long> {

    List<DeleteHistory> findAllByContentType(ContentType contentType);
}
