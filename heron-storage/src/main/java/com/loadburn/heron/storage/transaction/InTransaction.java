package com.loadburn.heron.storage.transaction;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-12
 */
public interface InTransaction {

    public void begin();

    public void commit();

    public void rollback();

    public boolean isActive();
}
