package com.loadburn.heron.storage.sql;

import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import com.loadburn.heron.storage.EntityStorage;
import com.loadburn.heron.storage.EntityStorageFactory;
import com.loadburn.heron.storage.StorageService;
import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.config.StorageDataSource;
import com.loadburn.heron.exceptions.ConvertException;
import com.loadburn.heron.storage.exceptions.LoadDriverException;
import com.loadburn.heron.storage.exceptions.StorageException;
import com.loadburn.heron.storage.sql.mysql.MySQLEntityStorageFactory;
import com.loadburn.heron.storage.transaction.InWork;
import com.loadburn.heron.utils.ConfigLoader;
import net.sf.ehcache.CacheManager;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-16
 */
public abstract class SQLStorageService implements Provider<EntityStorage>, StorageService, InWork {

    private final static String CONFIG_FILE = "storage.json";
    private volatile EntityStorageFactory esFactory;
    private CacheManager cacheManager;

    private final ThreadLocal<EntityStorage> entityStorage = new ThreadLocal<EntityStorage>();
    private final String persistenceConfigFile;
    private final Provider<ServletContext> contextProvider;
    private final EntityMetadata entityMetadata;
    private StorageDataSource dataSource;
    private ConfigLoader configLoader;

    public SQLStorageService(String persistenceConfigFile, Provider<ServletContext> contextProvider,
                             EntityMetadata entityMetadata) {
        this.persistenceConfigFile = persistenceConfigFile;
        this.contextProvider = contextProvider;
        this.entityMetadata = entityMetadata;

        initDataSource();
        initCache();
    }

    private void initDataSource() {
        configLoader = ConfigLoader.newInstance(contextProvider.get());
        String json;
        if (persistenceConfigFile != null) {
            json = configLoader.loadConfig(persistenceConfigFile);
        } else {
            json = configLoader.loadConfig(CONFIG_FILE);
        }

        try {
            dataSource = configLoader.convertJsonToTObject(json, StorageDataSource.class);
            if (dataSource != null)
                dataSource.startup();
        } catch (SQLException e) {
            throw new LoadDriverException("加载JDBC驱动异常", e);
        } catch (IOException e) {
            throw new ConvertException("JSON转换为指定对象出错", e);
        }
    }

    private void initCache() {
        if (dataSource != null) {
            String cacheFile = dataSource.getCacheFile();
            InputStream inputStream = cacheFile == null ? null : configLoader.loadConfigOfStream(cacheFile);
            if (inputStream != null) {
                cacheManager = CacheManager.create(inputStream);
            } else {
                throw new StorageException(String.format("缓存初始化失败,估计是因为没有[%s]文件或是打开文件出错",
                        cacheFile));
            }
        }
    }

    public boolean isWorking() {
        return entityStorage.get() != null;
    }

    @Override
    public EntityStorage get() {
        if (!isWorking()) {
            begin();
        }

        EntityStorage es = entityStorage.get();
        Preconditions.checkState(null != es, "Requested EntityStorage outside work unit. "
                + "Try calling Work.begin() first, or use a StorageFilter if you "
                + "are inside a servlet environment.");

        return es;
    }

    @Override
    public synchronized void start() {
        Preconditions.checkState(null == esFactory, "Persistence service was already initialized.");
        this.esFactory = new MySQLEntityStorageFactory(dataSource, entityMetadata, cacheManager);
    }

    @Override
    public synchronized void stop() {
        Preconditions.checkState(esFactory.isOpen(), "Persistence service was already shut down.");
        esFactory.close();
    }

    @Override
    public void begin() {
        Preconditions.checkState(null == entityStorage.get(),
                "Work already begun on this thread. Looks like you have called UnitOfWork.begin() twice"
                        + " without a balancing call to end() in between.");

        entityStorage.set(esFactory.createEntityStorage());
    }

    @Override
    public void end() {
        EntityStorage storage = entityStorage.get();
        if (null == storage) {
            return;
        }
        storage.close();
        entityStorage.remove();
    }

    public EntityStorageFactory getEsFactory() {
        return esFactory;
    }
}
