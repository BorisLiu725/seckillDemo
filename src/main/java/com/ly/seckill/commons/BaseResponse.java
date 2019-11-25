package com.ly.seckill.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by BorisLiu on 2019/11/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<E> {
    private int code;
    private E data;
}
