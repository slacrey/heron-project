package com.loadburn.heron.storage.sql.mysql;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.loadburn.heron.storage.EntityStorage;
import com.loadburn.heron.storage.annotations.Transactional;
import com.loadburn.heron.storage.cache.CacheUtils;
import com.loadburn.heron.storage.exceptions.StorageException;
import com.loadburn.heron.storage.transaction.InTransaction;
import com.loadburn.heron.storage.transaction.InWork;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-12
 */
class MySQLTransactionInterceptor implements MethodInterceptor {

    @Inject
    private final MySQLStorageService esProvider = null;

    @Inject
    private final InWork inWork = null;

    private final Map<String, Transactional> methodTransactionalMap = Maps.newHashMap();

    @Transactional
    private static class Internal {
    }

    private final ThreadLocal<Boolean> workState = new ThreadLocal<Boolean>();

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        if (!esProvider.isWorking()) {
            esProvider.begin();
            workState.set(true);
        }

        Transactional transactional = readTransactionMetadata(methodInvocation);
        EntityStorage es = this.esProvider.get();

        if (es.getTransaction().isActive()) {
            return methodInvocation.proceed();
        }

        final InTransaction esTransaction = es.getTransaction();
        esTransaction.begin();

        Object result;
        try {
            result = methodInvocation.proceed();

        } catch (Exception e) {
            if (rollbackIfNeed(transactional, e, esTransaction)) {
                esTransaction.commit();
            }
            throw new StorageException("Invocation拦截的方法执行错误", e);
        } finally {
            if (null != workState.get() && !esTransaction.isActive()) {
                workState.remove();
                inWork.end();
            }
        }

        try {
            esTransaction.commit();
        } finally {
            if (null != workState.get()) {
                workState.remove();
                inWork.end();
            }
        }
        return result;
    }

    /**
     * 读取方法的事务Annotation {@link Transactional}
     * @param methodInvocation 方法调用对象{@link MethodInvocation}
     * @return
     */
    private Transactional readTransactionMetadata(MethodInvocation methodInvocation) {

        EntityStorage es = this.esProvider.get();

        Method method = methodInvocation.getMethod();
        Class<?> targetClass = methodInvocation.getThis().getClass();

        String methodName = targetClass.getName() + "$" + method.getName().toLowerCase();
        Transactional transactional = (Transactional) CacheUtils.getCacheResult(es.getCache(), methodName);
        if (transactional != null) {
            return transactional;
        }

        transactional = method.getAnnotation(Transactional.class);
        if (null == transactional) {
            transactional = targetClass.getAnnotation(Transactional.class);
        }
        if (null == transactional) {
            transactional = Internal.class.getAnnotation(Transactional.class);
        }
        CacheUtils.putCacheResult(es.getCache(), methodName, transactional);

        return transactional;

    }

    /**
     *
     * 回滚
     * @param transactional {@link Transactional}
     * @param e     {@link Exception}
     * @param txn  {@link InTransaction}
     */
    private boolean rollbackIfNeed(Transactional transactional, Exception e,
                                   InTransaction txn) {
        boolean commit = true;

        for (Class<? extends Exception> rollBackOn : transactional.rollbackOn()) {

            if (rollBackOn.isInstance(e)) {
                commit = false;
                for (Class<? extends Exception> exceptOn : transactional.ignore()) {
                    if (exceptOn.isInstance(e)) {
                        commit = true;
                        break;
                    }
                }
                if (!commit) {
                    txn.rollback();
                }
                break;
            }
        }

        return commit;
    }
}
