package com.loadburn.heron.complier;

import com.google.common.collect.MapMaker;
import com.loadburn.heron.utils.ParsingUtils;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.Nullable;
import org.mvel2.CompileException;
import org.mvel2.MVEL;
import org.mvel2.PropertyAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

@ThreadSafe
public class MvelEvaluator implements Evaluator {

    private final Logger logger = LoggerFactory.getLogger(MvelEvaluator.class.getName());
    private final ConcurrentMap<String, Serializable> compiledExpressions = new MapMaker().makeMap();

    @Nullable
    public Object evaluate(String expr, Object bean) {
        Serializable compiled = compiledExpressions.get(expr);
        String message = null;
        if (null == compiled) {
            String preparedExpression = expr;

            if (ParsingUtils.isExpression(expr)) {
                preparedExpression = ParsingUtils.stripExpression(expr);
            }
            compiled = MVEL.compileExpression(preparedExpression);
            compiledExpressions.put(expr, compiled);
        }

        try {
            return MVEL.executeExpression(compiled, bean, new HashMap());
        } catch (PropertyAccessException e) {
            message = String.format("表达式 [%s] 不可读 (没有相应的getter方法?)", expr);
            throw new IllegalArgumentException(message, e);
        } catch (NullPointerException npe) {
            message = String.format("表达式 [%s] 的结果是[NullPointerException]",
                    expr);
            return throwException(message, npe);
        } catch (CompileException e) {
            message = String.format("表达式 [%s] 结果是非法的", expr);
            return throwException(message, e);
        }
    }

    private Object throwException(String mesage, Exception e) {
        logger.error(mesage, e);
        throw new IllegalArgumentException(mesage, e);
    }

    /**
     * 设置属性值
     * @param expr 属性
     * @param bean 类实例
     * @param value 值
     */
    public void write(String expr, Object bean, Object value) {
        MVEL.setProperty(bean, expr, value);
    }

    /**
     * 读取属性值
     * @param property 属性
     * @param contextObject bean对象
     * @return
     */
    public Object read(String property, Object contextObject) {
        return MVEL.getProperty(property, contextObject);
    }

}