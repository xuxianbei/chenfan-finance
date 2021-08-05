package com.chenfan.finance.utils.pageinfo;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.common.vo.Response;
import com.chenfan.finance.utils.pageinfo.model.CreateVo;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.enums.SqlKeyword.GROUP_BY;

/**
 * 期望功能：扩展mybatis自定义sql，查询条件从表字段中获取数据，
 * 注入到spring框架中去，后期只需要配置字段名称就可以实现
 * 未实现
 *
 * @author: xuxianbei
 * Date: 2021/3/16
 * Time: 9:34
 * Version:V1.0
 */
public abstract class AbstractBaseController<T> {

    @Resource
    List<BaseMapper> baseMapper;

    private T t;

    /**
     * 创建人
     *
     * @return
     */
    @GetMapping("/createNameList")
    public Response<List<CreateVo>> createNameList() {
        Wrapper<T> queryWrapper = new QueryWrapper(t, "create_by", "create_name");
        queryWrapper.getExpression().add(GROUP_BY);
        queryWrapper.getExpression().add(() -> "create_name");
        for (BaseMapper mapper : baseMapper) {
            System.out.println(mapper.getClass().getGenericSuperclass().getTypeName().equals(t.getClass().getSimpleName()));
        }

//        List<T> list = baseMapper.selectList(queryWrapper);
        List<T> list =new ArrayList<>();
        List<CreateVo> createVos = list.stream().map(key -> {
            CreateVo createVo = new CreateVo();
            BeanUtils.copyProperties(key, createVo);
            return createVo;
        }).collect(Collectors.toList());
        return PageInfoUtil.smartSuccess(createVos);
    }
}
