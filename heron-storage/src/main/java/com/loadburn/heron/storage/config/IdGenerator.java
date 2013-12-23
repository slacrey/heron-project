package com.loadburn.heron.storage.config;

import java.io.Serializable;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-15
 */
public interface IdGenerator extends Serializable {

    public long nextId();

}
