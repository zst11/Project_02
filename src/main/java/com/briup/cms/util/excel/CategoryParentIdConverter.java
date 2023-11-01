package com.briup.cms.util.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.briup.cms.bean.Category;
import com.briup.cms.exception.ServiceException;
import com.briup.cms.util.ResultCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author horry
 * @Description 栏目数据导入导出时, 对父栏目ID的处理
 * @date 2023/8/22-16:08
 */
@Slf4j
public class CategoryParentIdConverter implements Converter<Integer> {

    public static List<Category> list; // 这里的list在controller层中每次进行导入导出时都会赋值，也是属于更新


    /**
     * 开启对Integer的支持
     *
     * @return Integer.class
     */
    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class;
    }

    /**
     * Excel文件中单元格的数据类型-String
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 将单元格里的数据转为java对象,也就是 将父栏目名称转换为父栏目ID,用于导入时使用
     *
     * @param cellData            数据对象
     * @param contentProperty     单元格内容属性
     * @param globalConfiguration 全局配置对象
     * @return Integer
     */
    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                     GlobalConfiguration globalConfiguration) {
        String value = cellData.getStringValue();
        System.out.println(list);
        return converterStringToInteger(value);
    }

    /**
     * 将单元格里的数据转为java对象,也就是 将父栏目名称转换为父栏目ID,用于导入时使用
     *
     * @param context 读取转换器上下文
     * @return Integer
     */
    @Override
    public Integer convertToJavaData(ReadConverterContext<?> context) {
        String value = context.getReadCellData().getStringValue();
        return converterStringToInteger(value);
    }


    private Integer converterStringToInteger(String value) {
        log.info("转换器中测试看list中是否有值{}",list);
        if (value != null) {
            //获取所有的名称
            List<String> names = list.stream().map(Category::getName).collect(Collectors.toList());
            //将导入的数据中 父栏目名称 与 数据库中的父栏目名称进行对比, 如果存在则继续导入数据,不存在就抛出异常
            if (!names.contains(value)) {
                throw new ServiceException(ResultCode.PCATEGORY_IS_INVALID);
            }
            //从集合中根据 父栏目的名称 获取该名称 所对应的父栏目的 id
            return list.get(list.indexOf(new Category(value))).getId();
        }
        return null;
    }


    /**
     * 在导出时,将父栏目ID 转换为 父栏目名称
     *
     * @param value               父栏目ID
     * @param contentProperty     单元格内容属性
     * @param globalConfiguration 全局配置对象
     */
    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {
        return convertToExcelData(value);
    }

    /**
     * 在导出时,将父栏目ID 转换为 父栏目名称
     *
     * @param context 读取转换器上下文对象
     * @return Integer
     */
    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Integer> context) {
        Integer value = context.getValue();
        return convertToExcelData(value);
    }

    private WriteCellData<?> convertToExcelData(Integer value) {
        //在所有的父栏目中获取 栏目ID 与 待导出数据中父栏目ID 一致的 栏目【ID唯一】
        List<Category> categoryList = list.stream()
                .filter(cate -> Objects.equals(cate.getId(), value))
                .collect(Collectors.toList());
        //如果集合不为空
        if (!categoryList.isEmpty()) {
            //导出的是父栏目的名称
            return new WriteCellData<>(categoryList.get(0).getName());
        }
        //集合为空说明 该栏目为父栏目,直接返回为空
        return new WriteCellData<>("");
    }
}
