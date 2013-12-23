package com.loadburn.heron.storage.transaction;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-11
 */
public interface InWork {

    void begin();

    void end();

}
