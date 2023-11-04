package com.briup.cms.bean.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author horry
 * @Description 日志展示实体
 * @date 2023/8/25-9:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogVO {
	/**
	 * 操作用户
	 */
	@ExcelProperty("操作用户")
	private String username;
	/**
	 * 接口描述信息
	 */
	@ExcelProperty("接口描述信息")
	private String businessName;
	/**
	 * 请求接口
	 */
	@ExcelProperty("请求接口")
	private String requestUrl;
	/**
	 * 请求方式
	 */
	@ExcelProperty("请求方式")
	private String requestMethod;
	/**
	 * ip
	 */
	@ExcelProperty("ip")
	private String ip;
	/**
	 * 请求接口耗时
	 */
	@ExcelProperty("请求接口耗时")
	private Long spendTime;
	/**
	 * 创建时间
	 */
	@ExcelProperty("创建时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private LocalDateTime createTime;
	/**
	 * 响应参数
	 */
	@ExcelIgnore
	private String resultJson;
	/**
	 * 响应状态码
	 */
	@ExcelProperty("响应状态码")
	private Integer code;
	/**
	 * 响应消息
	 */
	@ExcelProperty("响应消息")
	private String msg;
}
