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

package io.vertx.core.http;

import io.vertx.codegen.annotations.Options;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.CaOptions;
import io.vertx.core.net.JKSOptions;
import io.vertx.core.net.KeyCertOptions;
import io.vertx.core.net.KeyStoreOptions;
import io.vertx.core.net.PKCS12Options;
import io.vertx.core.net.TrustStoreOptions;
import io.vertx.core.net.impl.SocketDefaults;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@Options
public class HttpClientOptions {

  private static final int DEFAULT_SENDBUFFERSIZE = -1;
  private static final int DEFAULT_RECEIVEBUFFERSIZE = -1;
  private static final boolean DEFAULT_REUSEADDRESS = true;
  private static final int DEFAULT_TRAFFICCLASS = -1;

  private int sendBufferSize = DEFAULT_SENDBUFFERSIZE;
  private int receiveBufferSize = DEFAULT_RECEIVEBUFFERSIZE;
  private boolean reuseAddress = DEFAULT_REUSEADDRESS;
  private int trafficClass = DEFAULT_TRAFFICCLASS;

  // TCP stuff
  private static SocketDefaults SOCK_DEFAULTS = SocketDefaults.instance;

  private static final boolean DEFAULT_TCPNODELAY = true;
  private static final boolean DEFAULT_TCPKEEPALIVE = SOCK_DEFAULTS.isTcpKeepAlive();
  private static final int DEFAULT_SOLINGER = SOCK_DEFAULTS.getSoLinger();

  private boolean tcpNoDelay = DEFAULT_TCPNODELAY;
  private boolean tcpKeepAlive = DEFAULT_TCPKEEPALIVE;
  private int soLinger = DEFAULT_SOLINGER;
  private boolean usePooledBuffers;
  private int idleTimeout;

  // SSL stuff

  private boolean ssl;
  private KeyStoreOptions keyStore;
  private TrustStoreOptions trustStore;
  private Set<String> enabledCipherSuites = new HashSet<>();

  private static final int DEFAULT_CONNECTTIMEOUT = 60000;

  // Client specific TCP stuff

  private int connectTimeout;

  // Client specific SSL stuff

  private boolean trustAll;
  private ArrayList<String> crlPaths;
  private ArrayList<Buffer> crlValues;

  private static final int DEFAULT_MAXPOOLSIZE = 5;
  private static final boolean DEFAULT_KEEPALIVE = true;

  // Client specific SSL stuff

  private boolean verifyHost = true;

  // HTTP stuff

  private int maxPoolSize;
  private boolean keepAlive;
  private boolean pipelining;
  private boolean tryUseCompression;

  public HttpClientOptions(HttpClientOptions other) {
    this.sendBufferSize = other.getSendBufferSize();
    this.receiveBufferSize = other.getReceiveBufferSize();
    this.reuseAddress = other.isReuseAddress();
    this.trafficClass = other.getTrafficClass();
    this.tcpNoDelay = other.isTcpNoDelay();
    this.tcpKeepAlive = other.isTcpKeepAlive();
    this.soLinger = other.getSoLinger();
    this.usePooledBuffers = other.isUsePooledBuffers();
    this.idleTimeout = other.getIdleTimeout();
    this.ssl = other.isSsl();
    this.keyStore = other.getKeyStoreOptions() != null ? other.getKeyStoreOptions().clone() : null;
    this.trustStore = other.getTrustStoreOptions() != null ? other.getTrustStoreOptions().clone() : null;
    this.enabledCipherSuites = other.getEnabledCipherSuites() == null ? null : new HashSet<>(other.getEnabledCipherSuites());
    this.connectTimeout = other.getConnectTimeout();
    this.trustAll = other.isTrustAll();
    this.crlPaths = new ArrayList<>(other.getCrlPaths());
    this.crlValues = new ArrayList<>(other.getCrlValues());
    this.verifyHost = other.isVerifyHost();
    this.maxPoolSize = other.getMaxPoolSize();
    this.keepAlive = other.isKeepAlive();
    this.pipelining = other.isPipelining();
    this.tryUseCompression = other.isTryUseCompression();
  }

  public HttpClientOptions(JsonObject json) {
    this.sendBufferSize = json.getInteger("sendBufferSize", DEFAULT_SENDBUFFERSIZE);
    this.receiveBufferSize = json.getInteger("receiveBufferSize", DEFAULT_RECEIVEBUFFERSIZE);
    this.reuseAddress = json.getBoolean("reuseAddress", DEFAULT_REUSEADDRESS);
    this.trafficClass = json.getInteger("trafficClass", DEFAULT_TRAFFICCLASS);
    this.tcpNoDelay = json.getBoolean("tcpNoDelay", DEFAULT_TCPNODELAY);
    this.tcpKeepAlive = json.getBoolean("tcpKeepAlive", DEFAULT_TCPKEEPALIVE);
    this.soLinger = json.getInteger("soLinger", DEFAULT_SOLINGER);
    this.usePooledBuffers = json.getBoolean("usePooledBuffers", false);
    this.idleTimeout = json.getInteger("idleTimeout", 0);
    this.ssl = json.getBoolean("ssl", false);
    JsonObject keyStoreJson = json.getObject("keyStoreOptions");
    if (keyStoreJson != null) {
      String type = keyStoreJson.getString("type", null);
      switch (type != null ? type.toLowerCase() : "jks") {
        case "jks":
          keyStore = new JKSOptions(keyStoreJson);
          break;
        case "pkcs12":
          keyStore = new PKCS12Options(keyStoreJson);
          break;
        case "keycert":
          keyStore = new KeyCertOptions(keyStoreJson);
          break;
        default:
          throw new IllegalArgumentException("Invalid key store type: " + type);
      }
    }
    JsonObject trustStoreJson = json.getObject("trustStoreOptions");
    if (trustStoreJson != null) {
      String type = trustStoreJson.getString("type", null);
      switch (type != null ? type.toLowerCase() : "jks") {
        case "jks":
          trustStore = new JKSOptions(trustStoreJson);
          break;
        case "pkcs12":
          trustStore = new PKCS12Options(trustStoreJson);
          break;
        case "ca":
          trustStore = new CaOptions(trustStoreJson);
          break;
        default:
          throw new IllegalArgumentException("Invalid trust store type: " + type);
      }
    }
    JsonArray arr = json.getArray("enabledCipherSuites");
    this.enabledCipherSuites = arr == null ? null : new HashSet<String>(arr.toList());
    this.connectTimeout = json.getInteger("connectTimeout", DEFAULT_CONNECTTIMEOUT);
    this.trustAll = json.getBoolean("trustAll", false);
    arr = json.getArray("crlPaths");
    this.crlPaths = arr == null ? new ArrayList<>() : new ArrayList<String>(arr.toList());
    arr = json.getArray("crlValues");
    this.crlValues = new ArrayList<>();
    if (arr != null) {
      ((List<byte[]>)arr.toList()).stream().map(Buffer::buffer).forEach(crlValues::add);
    }
    this.verifyHost = json.getBoolean("verifyHost", true);
    this.maxPoolSize = json.getInteger("maxPoolSize", DEFAULT_MAXPOOLSIZE);
    this.keepAlive = json.getBoolean("keepAlive", DEFAULT_KEEPALIVE);
    this.pipelining = json.getBoolean("pipelining", false);
    this.tryUseCompression = json.getBoolean("tryUseCompression", false);
  }

  public HttpClientOptions() {
    sendBufferSize = DEFAULT_SENDBUFFERSIZE;
    receiveBufferSize = DEFAULT_RECEIVEBUFFERSIZE;
    reuseAddress = DEFAULT_REUSEADDRESS;
    trafficClass = DEFAULT_TRAFFICCLASS;
    tcpNoDelay = DEFAULT_TCPNODELAY;
    tcpKeepAlive = DEFAULT_TCPKEEPALIVE;
    soLinger = DEFAULT_SOLINGER;
    connectTimeout = DEFAULT_CONNECTTIMEOUT;
    crlPaths = new ArrayList<>();
    crlValues = new ArrayList<>();
    maxPoolSize = DEFAULT_MAXPOOLSIZE;
    keepAlive = DEFAULT_KEEPALIVE;
  }

  public int getSendBufferSize() {
    return sendBufferSize;
  }

  public HttpClientOptions setSendBufferSize(int sendBufferSize) {
    if (sendBufferSize < 1) {
      throw new IllegalArgumentException("sendBufferSize must be > 0");
    }
    this.sendBufferSize = sendBufferSize;
    return this;
  }

  public int getReceiveBufferSize() {
    return receiveBufferSize;
  }

  public HttpClientOptions setReceiveBufferSize(int receiveBufferSize) {
    if (receiveBufferSize < 1) {
      throw new IllegalArgumentException("receiveBufferSize must be > 0");
    }
    this.receiveBufferSize = receiveBufferSize;
    return this;
  }

  public boolean isReuseAddress() {
    return reuseAddress;
  }

  public HttpClientOptions setReuseAddress(boolean reuseAddress) {
    this.reuseAddress = reuseAddress;
    return this;
  }

  public int getTrafficClass() {
    return trafficClass;
  }

  public HttpClientOptions setTrafficClass(int trafficClass) {
    if (trafficClass < 0 || trafficClass > 255) {
      throw new IllegalArgumentException("trafficClass tc must be 0 <= tc <= 255");
    }
    this.trafficClass = trafficClass;
    return this;
  }

  public boolean isTcpNoDelay() {
    return tcpNoDelay;
  }

  public HttpClientOptions setTcpNoDelay(boolean tcpNoDelay) {
    this.tcpNoDelay = tcpNoDelay;
    return this;
  }

  public boolean isTcpKeepAlive() {
    return tcpKeepAlive;
  }

  public HttpClientOptions setTcpKeepAlive(boolean tcpKeepAlive) {
    this.tcpKeepAlive = tcpKeepAlive;
    return this;
  }

  public int getSoLinger() {
    return soLinger;
  }

  public HttpClientOptions setSoLinger(int soLinger) {
    if (soLinger < 0) {
      throw new IllegalArgumentException("soLinger must be >= 0");
    }
    this.soLinger = soLinger;
    return this;
  }

  public boolean isUsePooledBuffers() {
    return usePooledBuffers;
  }

  public HttpClientOptions setUsePooledBuffers(boolean usePooledBuffers) {
    this.usePooledBuffers = usePooledBuffers;
    return this;
  }

  public HttpClientOptions setIdleTimeout(int idleTimeout) {
    if (idleTimeout < 0) {
      throw new IllegalArgumentException("idleTimeout must be >= 0");
    }
    this.idleTimeout = idleTimeout;
    return this;
  }

  public int getIdleTimeout() {
    return idleTimeout;
  }

  public boolean isSsl() {
    return ssl;
  }

  public HttpClientOptions setSsl(boolean ssl) {
    this.ssl = ssl;
    return this;
  }

  public KeyStoreOptions getKeyStoreOptions() {
    return keyStore;
  }

  public HttpClientOptions setKeyStoreOptions(KeyStoreOptions keyStore) {
    this.keyStore = keyStore;
    return this;
  }

  public TrustStoreOptions getTrustStoreOptions() {
    return trustStore;
  }

  public HttpClientOptions setTrustStoreOptions(TrustStoreOptions trustStore) {
    this.trustStore = trustStore;
    return this;
  }

  public HttpClientOptions addEnabledCipherSuite(String suite) {
    enabledCipherSuites.add(suite);
    return this;
  }

  public Set<String> getEnabledCipherSuites() {
    return enabledCipherSuites;
  }

  public boolean isTrustAll() {
    return trustAll;
  }

  public HttpClientOptions setTrustAll(boolean trustAll) {
    this.trustAll = trustAll;
    return this;
  }

  public List<String> getCrlPaths() {
    return crlPaths;
  }

  public HttpClientOptions addCrlPath(String crlPath) throws NullPointerException {
    Objects.requireNonNull(crlPath, "No null crl accepted");
    crlPaths.add(crlPath);
    return this;
  }

  public List<Buffer> getCrlValues() {
    return crlValues;
  }

  public HttpClientOptions addCrlValue(Buffer crlValue) throws NullPointerException {
    Objects.requireNonNull(crlValue, "No null crl accepted");
    crlValues.add(crlValue);
    return this;
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public HttpClientOptions setConnectTimeout(int connectTimeout) {
    if (connectTimeout < 0) {
      throw new IllegalArgumentException("connectTimeout must be >= 0");
    }
    this.connectTimeout = connectTimeout;
    return this;
  }

  public int getMaxPoolSize() {
    return maxPoolSize;
  }

  public HttpClientOptions setMaxPoolSize(int maxPoolSize) {
    if (maxPoolSize < 1) {
      throw new IllegalArgumentException("maxPoolSize must be > 0");
    }
    this.maxPoolSize = maxPoolSize;
    return this;
  }

  public boolean isKeepAlive() {
    return keepAlive;
  }

  public HttpClientOptions setKeepAlive(boolean keepAlive) {
    this.keepAlive = keepAlive;
    return this;
  }

  public boolean isPipelining() {
    return pipelining;
  }

  public HttpClientOptions setPipelining(boolean pipelining) {
    this.pipelining = pipelining;
    return this;
  }

  public boolean isVerifyHost() {
    return verifyHost;
  }

  public HttpClientOptions setVerifyHost(boolean verifyHost) {
    this.verifyHost = verifyHost;
    return this;
  }

  public boolean isTryUseCompression() {
    return tryUseCompression;
  }

  public HttpClientOptions setTryUseCompression(boolean tryUseCompression) {
    this.tryUseCompression = tryUseCompression;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    HttpClientOptions that = (HttpClientOptions) o;

    if (connectTimeout != that.connectTimeout) return false;
    if (idleTimeout != that.idleTimeout) return false;
    if (keepAlive != that.keepAlive) return false;
    if (maxPoolSize != that.maxPoolSize) return false;
    if (pipelining != that.pipelining) return false;
    if (receiveBufferSize != that.receiveBufferSize) return false;
    if (reuseAddress != that.reuseAddress) return false;
    if (sendBufferSize != that.sendBufferSize) return false;
    if (soLinger != that.soLinger) return false;
    if (ssl != that.ssl) return false;
    if (tcpKeepAlive != that.tcpKeepAlive) return false;
    if (tcpNoDelay != that.tcpNoDelay) return false;
    if (trafficClass != that.trafficClass) return false;
    if (trustAll != that.trustAll) return false;
    if (tryUseCompression != that.tryUseCompression) return false;
    if (usePooledBuffers != that.usePooledBuffers) return false;
    if (verifyHost != that.verifyHost) return false;
    if (crlPaths != null ? !crlPaths.equals(that.crlPaths) : that.crlPaths != null) return false;
    if (crlValues != null ? !crlValues.equals(that.crlValues) : that.crlValues != null) return false;
    if (enabledCipherSuites != null ? !enabledCipherSuites.equals(that.enabledCipherSuites) : that.enabledCipherSuites != null)
      return false;
    if (keyStore != null ? !keyStore.equals(that.keyStore) : that.keyStore != null) return false;
    if (trustStore != null ? !trustStore.equals(that.trustStore) : that.trustStore != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = sendBufferSize;
    result = 31 * result + receiveBufferSize;
    result = 31 * result + (reuseAddress ? 1 : 0);
    result = 31 * result + trafficClass;
    result = 31 * result + (tcpNoDelay ? 1 : 0);
    result = 31 * result + (tcpKeepAlive ? 1 : 0);
    result = 31 * result + soLinger;
    result = 31 * result + (usePooledBuffers ? 1 : 0);
    result = 31 * result + idleTimeout;
    result = 31 * result + (ssl ? 1 : 0);
    result = 31 * result + (keyStore != null ? keyStore.hashCode() : 0);
    result = 31 * result + (trustStore != null ? trustStore.hashCode() : 0);
    result = 31 * result + (enabledCipherSuites != null ? enabledCipherSuites.hashCode() : 0);
    result = 31 * result + connectTimeout;
    result = 31 * result + (trustAll ? 1 : 0);
    result = 31 * result + (crlPaths != null ? crlPaths.hashCode() : 0);
    result = 31 * result + (crlValues != null ? crlValues.hashCode() : 0);
    result = 31 * result + (verifyHost ? 1 : 0);
    result = 31 * result + maxPoolSize;
    result = 31 * result + (keepAlive ? 1 : 0);
    result = 31 * result + (pipelining ? 1 : 0);
    result = 31 * result + (tryUseCompression ? 1 : 0);
    return result;
  }
}