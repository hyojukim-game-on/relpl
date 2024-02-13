package com.ssafy.relpl.service.result;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleResult<T> extends CommonResult {
    private T data;
}
