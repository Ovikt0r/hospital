package com.oviktor.dao.utils;


import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Pagination {

    private final int pageNum;
    private final int pageSize;

    private Pagination(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public static Pagination pageNum(int pageNum) {return new Pagination(pageNum, 10);
    }
}
