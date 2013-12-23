/*
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 13-12-11
 * Time: 下午4:08
 */
package com.loadburn.heron.storage;

import com.google.inject.AbstractModule;
import com.loadburn.heron.storage.annotations.Transactional;
import com.loadburn.heron.storage.transaction.InWork;
import com.loadburn.heron.utils.generics.ClassesUtils;
import org.aopalliance.intercept.MethodInterceptor;

import javax.persistence.Entity;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

public abstract class StorageModule extends AbstractModule {

    protected final Set<Class<?>> entityClasses = new LinkedHashSet<Class<?>>();

    protected void configure() {
        configurePersistence();

        requireBinding(StorageService.class);
        requireBinding(InWork.class);

        // 类级别 @Transacational
        bindInterceptor(annotatedWith(Transactional.class), any(), getTransactionInterceptor());
        // 方法级别 @Transacational
        bindInterceptor(any(), annotatedWith(Transactional.class), getTransactionInterceptor());
    }

    protected abstract void configurePersistence();

    protected abstract MethodInterceptor getTransactionInterceptor();

    protected final void scanEntity(Package tree) {
        Set<Class<?>> classes = ClassesUtils.matching(annotatedWith(Entity.class)).in(tree);
        for (Class<?> clazz : classes) {
            addPersistent(clazz);
        }
    }

    protected final void addPersistent(Class<?> entity) {
        if (entityClasses.contains(entity)) {
            addError("Entity class was added more than once: "
                    + entity.getName()
                    + "; to the same persistence module: "
                    + getClass().getSimpleName());
            return;
        }
        entityClasses.add(entity);
    }

}
