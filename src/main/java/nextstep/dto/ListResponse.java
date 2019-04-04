package nextstep.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class ListResponse<T> {
    private List<T> list;
    private Integer page;
    private Integer size;

    private Integer totalPages;
    private Long totalElements;

    public ListResponse() {
    }

    public ListResponse(Page<T> result) {
        list = result.getContent();
        page = result.getNumber();
        size = result.getSize();
        totalPages = result.getTotalPages();
        totalElements = result.getTotalElements();
    }

    public List<T> getList() {
        return list;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getSize() {
        return size;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public Long getTotalElements() {
        return totalElements;
    }
}
