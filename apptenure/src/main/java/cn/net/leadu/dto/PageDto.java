package cn.net.leadu.dto;

import lombok.Data;

/**
 * Created by qiaohao on 2017/3/3.
 */
@Data
public class PageDto {
    private int page;
    private int size;
    private Object content;
    private long totalElements;

    public PageDto(){}

    public PageDto(long totalElements, Object content){
        this.totalElements = totalElements;
        this.content = content;
    }
}
