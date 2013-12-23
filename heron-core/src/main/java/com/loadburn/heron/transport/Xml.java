package com.loadburn.heron.transport;

import com.google.inject.ImplementedBy;

@ImplementedBy(XStreamXmlTransport.class)
public abstract class Xml implements Transport {

  public String contentType() {
    return "text/xml";
  }
}