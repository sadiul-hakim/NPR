package xyz.sadiulhakim.npr.pojo;

import java.util.ArrayList;
import java.util.List;

public class PaginationResult {
    private List<Object> data = new ArrayList<>();
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrev;
    private long totalRecords;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private int start;
    private long end;

    public PaginationResult() {
    }

    public PaginationResult(List<Object> data, boolean isFirst, boolean isLast,
                            boolean hasNext, boolean hasPrev, long totalRecords, int totalPages,
                            int currentPage, int pageSize, int start, long end) {
        this.data = data;
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.hasNext = hasNext;
        this.hasPrev = hasPrev;
        this.totalRecords = totalRecords;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.start = start;
        this.end = end;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrev() {
        return hasPrev;
    }

    public void setHasPrev(boolean hasPrev) {
        this.hasPrev = hasPrev;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "PaginationResult{" +
                "data=" + data +
                ", isFirst=" + isFirst +
                ", isLast=" + isLast +
                ", hasNext=" + hasNext +
                ", hasPrev=" + hasPrev +
                ", totalRecords=" + totalRecords +
                ", totalPages=" + totalPages +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}