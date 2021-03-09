package com.example.position.vo;

import com.example.position.enumvo.ApiResultStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResultVo {
	private int code;
	private String msg;
	private Object data;
	
	public static ResultVo success() {
		return success(null);
	}
	
	public static ResultVo success(Object data) {
		return new ResultVo()
				.setCode(ApiResultStatus.SUCCESS.getCode())
				.setMsg(ApiResultStatus.SUCCESS.getMsg())
				.setData(data);
	}
	
	public static ResultVo error() {
		return failed(ApiResultStatus.ERROR);
	}
	
	public static ResultVo error(String msg, Object... args) {
		return create(ApiResultStatus.ERROR.getCode(), msg, args);
	}
	
	public static ResultVo failed(ApiResultStatus status, Object... args) {
		return create(status.getCode(), status.getMsg(), args);
	}
	
	public static ResultVo failed(BoException e) {
		return create(e.getCode(), e.getMessage());
	}
	
	private static ResultVo create(int code, String msg, Object... args) {
		if (args.length > 0) {
			return new ResultVo()
					.setCode(code)
					.setMsg(String.format(msg, args));
		} else {
			return new ResultVo()
					.setCode(code)
					.setMsg(msg);
		}
	}
}
