package com.briup.cms.util.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.briup.cms.bean.Category;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author horry
 * @Description 导入栏目数据的监听器
 * @date 2023/8/22-15:39
 */
public class CategoryListener extends AnalysisEventListener<Category> {
    //存储栏目名称的集合
    List<String> names = new ArrayList<>();

    /**
     * 每解析一行，回调该方法
     *
     * @param category 从excel中导入的栏目对象
     * @param context 分析上下文
     */
    @Override
    public void invoke(Category category, AnalysisContext context) {
        //校验名称
        String name = category.getName();
        //判断栏目名是否有效
        if (!StringUtils.hasText(name)) {
            throw new RuntimeException(String.format("第%s行名称为空，请核实", context.readRowHolder().getRowIndex() + 1));
        }
        //判断栏目名是否重复存在
        if (names.contains(name)) {
            throw new RuntimeException(String.format("第%s行名称已重复，请核实", context.readRowHolder().getRowIndex() + 1));
        } else {
            names.add(name);
        }
    }

    /**
     * 出现异常回调
     *
     * @param exception 存在的异常
     * @param context 分析上下文
     * @throws Exception 抛出的异常
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        if (exception instanceof ExcelDataConvertException) {
            //从0开始计算
            int columnIndex = ((ExcelDataConvertException) exception).getColumnIndex() + 1;
            int rowIndex = ((ExcelDataConvertException) exception).getRowIndex() + 1;
            String message = "第" + rowIndex + "行，第" + columnIndex + "列" + "数据格式有误，请核实";

            exception.printStackTrace();
            throw new RuntimeException(message);

        } else if (exception instanceof RuntimeException) {
            throw exception;
        } else {
            super.onException(exception, context);
        }
    }

    /**
     * 解析完,全部回调
     *
     * @param context 分析上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //解析完,全部回调逻辑实现
        names.clear();
    }
}
