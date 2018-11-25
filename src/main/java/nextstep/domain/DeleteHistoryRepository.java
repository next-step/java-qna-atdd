package nextstep.domain;

import org.springframework.data.repository.CrudRepository;

public interface DeleteHistoryRepository extends CrudRepository<DeleteHistory, Long> {
	long countByContentType(ContentType contentType);
}
