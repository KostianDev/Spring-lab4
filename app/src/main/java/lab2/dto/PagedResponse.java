package lab2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Сторінкова відповідь для пагінації")
public class PagedResponse<T> {
    
    @Schema(description = "Список елементів на поточній сторінці")
    private List<T> content;
    
    @Schema(description = "Номер поточної сторінки (починаючи з 0)", example = "0")
    private int page;
    
    @Schema(description = "Розмір сторінки (кількість елементів)", example = "10")
    private int size;
    
    @Schema(description = "Загальна кількість елементів", example = "25")
    private long totalElements;
    
    @Schema(description = "Загальна кількість сторінок", example = "3")
    private int totalPages;
    
    @Schema(description = "Чи є ця сторінка першою", example = "true")
    private boolean first;
    
    @Schema(description = "Чи є ця сторінка останньою", example = "false")
    private boolean last;

    public PagedResponse() {
    }

    public PagedResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.first = page == 0;
        this.last = page >= totalPages - 1;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
