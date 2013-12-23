/*
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 13-12-12
 * Time: 上午8:57
 */
package com.loadburn.heron.storage.sql.mysql;

import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.loadburn.heron.storage.*;
import com.loadburn.heron.storage.annotations.DataConfigFile;
import com.loadburn.heron.storage.config.DataBaseType;
import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.transaction.InWork;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.Collections;
import java.util.Set;

import static com.google.inject.matcher.Matchers.annotatedWith;

public abstract class MySQLStorageModule extends StorageModule {

    private final static DataBaseType dataBaseType = DataBaseType.MySQL;
    private final String configFile;
    private MethodInterceptor transactionInterceptor;

    public MySQLStorageModule(String configFile) {
        this.configFile = configFile;
    }

    @Override
    protected void configurePersistence() {

        configureEntity();

        bind(new TypeLiteral<Set<Class<?>>>() {}).toInstance(Collections.unmodifiableSet(entityClasses));
        bind(new TypeLiteral<DataBaseType>() {}).toInstance(dataBaseType);

        bind(EntityMetadata.class).in(Singleton.class);

        bindConstant().annotatedWith(DataConfigFile.class).to(configFile);

        bind(MySQLStorageService.class).in(Singleton.class);

        bind(StorageService.class).to(MySQLStorageService.class);
        bind(InWork.class).to(MySQLStorageService.class);
        bind(EntityStorage.class).toProvider(MySQLStorageService.class);
        bind(EntityStorageFactory.class)
                .toProvider(MySQLStorageService.EntityStorageFactoryProvider.class);

        transactionInterceptor = new MySQLTransactionInterceptor();
        requestInjection(transactionInterceptor);

    }

    @Override
    protected MethodInterceptor getTransactionInterceptor() {
        return transactionInterceptor;
    }

    protected abstract void configureEntity();


}
