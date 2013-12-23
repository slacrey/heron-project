package com.loadburn.heron.complier;

import com.google.inject.ImplementedBy;
import org.jetbrains.annotations.Nullable;

@ImplementedBy(MvelEvaluator.class)
public interface Evaluator {

    @Nullable
    Object evaluate(String expr, Object bean);

    /**
     * 设置属性值
     * @param expr 属性
     * @param bean 类实例
     * @param value 值
     */
    void write(String expr, Object bean, Object value);

    /**
     * 读取bean中的属性值
     * @param property 属性
     * @param contextObject bean对象
     * @return 返回属性值
     */
    Object read(String property, Object contextObject);
}
