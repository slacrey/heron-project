package com.loadburn.heron.storage.sql.mysql;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.EntityStorageFactory;
import com.loadburn.heron.storage.annotations.DataConfigFile;
import com.loadburn.heron.storage.sql.SQLStorageService;

import javax.servlet.ServletContext;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-11
 */
@Singleton
public class MySQLStorageService extends SQLStorageService {

    @Inject
    public MySQLStorageService(@DataConfigFile String persistenceConfigFile, Provider<ServletContext> contextProvider,
                               EntityMetadata entityMetadata) {
        super(persistenceConfigFile, contextProvider, entityMetadata);
    }

    @Singleton
    public static class EntityStorageFactoryProvider implements Provider<EntityStorageFactory> {
        private final MySQLStorageService esProvider;

        @Inject
        public EntityStorageFactoryProvider(MySQLStorageService esProvider) {
            this.esProvider = esProvider;
        }

        public EntityStorageFactory get() {
            assert null != esProvider.getEsFactory();
            return esProvider.getEsFactory();
        }
    }

}
