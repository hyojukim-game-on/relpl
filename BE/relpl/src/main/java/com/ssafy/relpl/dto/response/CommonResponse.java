package com.ssafy.relpl.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Coinbarcode 와 Coinscore 중복으로 사용.
public class CommonResponse<T> {

    private Integer code;
    private String message;
    private T data;

    public static <T> CommonResponse<T> OK(T data){
        var response = new CommonResponse();
        response.setCode(200);
        response.setMessage("");
        response.setData(data);
        return response;
    }

    public static <T> CommonResponse<T> OK(String msg, T data){
        var response = new CommonResponse();
        response.setCode(200);
        response.setMessage(msg);
        response.setData(data);
        return response;
    }

    public static <T> CommonResponse WELL_CREATED(T data){
        var response = new CommonResponse();
        response.setCode(201);
        response.setMessage("");
        response.setData(data);
        return response;
    }

    public static <T> CommonResponse WELL_CREATED(String msg, T data){
        var response = new CommonResponse();
        response.setCode(201);
        response.setMessage(msg);
        response.setData(data);
        return response;
    }

}
