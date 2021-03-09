package com.example.position.vo;



import com.example.position.enumvo.ApiResultStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 传输的分页数据
 * @param <T>
 */

@Data
@Accessors(chain = true)
public class PageVo<T> {
    private int code;
    private String msg;
    private int count;
    private Object data;

    public static PageVo success() {
        return success(null);
    }

    public static PageVo success(Object data) {
        return new PageVo()
                .setCode(ApiResultStatus.SUCCESS.getCode())
                .setMsg(ApiResultStatus.SUCCESS.getMsg())
                .setData(data);
    }

    public static PageVo success(Object data,Integer count) {
        return new PageVo()
                .setCode(ApiResultStatus.SUCCESS.getCode())
                .setMsg(ApiResultStatus.SUCCESS.getMsg())
                .setCount(count)
                .setData(data);
    }

    public static PageVo error() {
        return failed(ApiResultStatus.ERROR);
    }

    public static PageVo error(String msg, Object... args) {
        return create(ApiResultStatus.ERROR.getCode(), msg, args);
    }

    public static PageVo failed(ApiResultStatus status, Object... args) {
        return create(status.getCode(), status.getMsg(), args);
    }

    public static PageVo failed(BoException e) {
        return create(e.getCode(), e.getMessage());
    }

    private static PageVo create(int code, String msg, Object... args) {
        if (args.length > 0) {
            return new PageVo()
                    .setCode(code)
                    .setMsg(String.format(msg, args));
        } else {
            return new PageVo()
                    .setCode(code)
                    .setMsg(msg);
        }
    }
}
