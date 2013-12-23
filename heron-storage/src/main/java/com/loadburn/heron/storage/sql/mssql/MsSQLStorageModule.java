package com.loadburn.heron.storage.sql.mssql;

import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.loadburn.heron.storage.EntityStorage;
import com.loadburn.heron.storage.EntityStorageFactory;
import com.loadburn.heron.storage.StorageModule;
import com.loadburn.heron.storage.StorageService;
import com.loadburn.heron.storage.annotations.DataConfigFile;
import com.loadburn.heron.storage.config.DataBaseType;
import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.transaction.InWork;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.Collections;
import java.util.Set;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-16
 */
public abstract class MsSQLStorageModule extends StorageModule {

    private final static DataBaseType dataBaseType = DataBaseType.MsSQL;
    private final String configFile;
    private MethodInterceptor transactionInterceptor;

    protected MsSQLStorageModule(String configFile) {
        this.configFile = configFile;
    }

    @Override
    protected void configurePersistence() {
        configureEntity();

        bind(new TypeLiteral<Set<Class<?>>>() {}).toInstance(Collections.unmodifiableSet(entityClasses));
        bind(new TypeLiteral<DataBaseType>() {}).toInstance(dataBaseType);

        bind(EntityMetadata.class).in(Singleton.class);

        bindConstant().annotatedWith(DataConfigFile.class).to(configFile);

        bind(MsSQLStorageService.class).in(Singleton.class);

        bind(StorageService.class).to(MsSQLStorageService.class);
        bind(InWork.class).to(MsSQLStorageService.class);
        bind(EntityStorage.class).toProvider(MsSQLStorageService.class);
        bind(EntityStorageFactory.class)
                .toProvider(MsSQLStorageService.EntityStorageFactoryProvider.class);

        transactionInterceptor = new MsSQLTransactionInterceptor();
        requestInjection(transactionInterceptor);
    }

    @Override
    protected MethodInterceptor getTransactionInterceptor() {
        return transactionInterceptor;
    }

    protected abstract void configureEntity();
}
