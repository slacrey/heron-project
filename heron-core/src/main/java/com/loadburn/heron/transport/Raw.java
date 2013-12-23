package com.loadburn.heron.transport;

import com.google.inject.ImplementedBy;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-29
 */
@ImplementedBy(ByteArrayTransport.class)
public abstract class Raw implements Transport {

  public String contentType() {
    return "application/octet-stream";
  }
}