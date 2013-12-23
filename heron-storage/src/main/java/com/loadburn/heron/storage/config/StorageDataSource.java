package com.loadburn.heron.storage.config;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.PoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-20
 */
public class StorageDataSource extends BoneCPConfig {

    private static final Logger logger = LoggerFactory.getLogger(StorageDataSource.class);
    private String driverClass;
    private String cacheFile;

    public BoneCPConfig getConfig() {
        return this;
    }

    public StorageDataSource() {
    }

    public void startup() throws SQLException {
        try {
            if (this.getDriverClass() != null) {
                loadClass(this.getDriverClass());
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException(PoolUtil.stringifyException(e));
        }
    }


    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getCacheFile() {
        return cacheFile;
    }

    public void setCacheFile(String cacheFile) {
        this.cacheFile = cacheFile;
    }
}
