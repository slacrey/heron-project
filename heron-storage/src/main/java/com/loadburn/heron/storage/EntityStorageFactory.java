package com.loadburn.heron.storage;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-11
 */
public interface EntityStorageFactory {

    EntityStorage createEntityStorage();

    void close();

    public boolean isOpen();

}
