package com.briup.cms.bean.extend;

import com.briup.cms.bean.Category;
import lombok.Data;

import java.util.List;

@Data
public class CategoryExtend extends Category {

    List<Category> cates;
}
