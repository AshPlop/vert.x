/*
 * Copyright (c) 2011-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package io.vertx.core.net;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@DataObject
public class ConnectOptions {

  public static final String DEFAULT_HOST = "0.0.0.0";
  public static final int DEFAULT_PORT = -1;
  public static final boolean DEFAULT_SSL = false;

  private String host;
  private int port;
  private boolean ssl;

  public ConnectOptions() {
    host = DEFAULT_HOST;
    port = DEFAULT_PORT;
    ssl = DEFAULT_SSL;
  }

  public ConnectOptions(ConnectOptions other) {
    setHost(other.host);
    setPort(other.port);
    setSsl(other.ssl);

  }

  public ConnectOptions(JsonObject json) {
    setHost(json.getString("host", DEFAULT_HOST));
    setPort(json.getInteger("port", DEFAULT_PORT));
    setSsl(json.getBoolean("ssl", DEFAULT_SSL));
  }

  public String getHost() {
    return host;
  }

  public ConnectOptions setHost(String host) {
    this.host = host;
    return this;
  }

  public int getPort() {
    return port;
  }

  public ConnectOptions setPort(int port) {
    this.port = port;
    return this;
  }

  public boolean isSsl() {
    return ssl;
  }

  public ConnectOptions setSsl(boolean ssl) {
    this.ssl = ssl;
    return this;
  }
}
