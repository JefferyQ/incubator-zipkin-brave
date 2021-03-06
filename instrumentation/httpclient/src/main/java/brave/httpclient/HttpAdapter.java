/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package brave.httpclient;

import brave.Span;
import java.net.InetAddress;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestWrapper;

final class HttpAdapter extends brave.http.HttpClientAdapter<HttpRequestWrapper, HttpResponse> {
  @Override public String method(HttpRequestWrapper request) {
    return request.getRequestLine().getMethod();
  }

  @Override public String path(HttpRequestWrapper request) {
    String result = request.getURI().getPath();
    int queryIndex = result.indexOf('?');
    return queryIndex == -1 ? result : result.substring(0, queryIndex);
  }

  @Override public String url(HttpRequestWrapper request) {
    HttpHost target = request.getTarget();
    if (target != null) return target.toURI() + request.getURI();
    return request.getRequestLine().getUri();
  }

  @Override public String requestHeader(HttpRequestWrapper request, String name) {
    Header result = request.getFirstHeader(name);
    return result != null ? result.getValue() : null;
  }

  @Override public Integer statusCode(HttpResponse response) {
    return statusCodeAsInt(response);
  }

  @Override public int statusCodeAsInt(HttpResponse response) {
    return response.getStatusLine().getStatusCode();
  }

  static void parseTargetAddress(HttpRequestWrapper httpRequest, Span span) {
    if (span.isNoop()) return;
    HttpHost target = httpRequest.getTarget();
    if (target == null) return;
    InetAddress address = target.getAddress();
    if (address != null) {
      if (span.remoteIpAndPort(address.getHostAddress(), target.getPort())) return;
    }
    span.remoteIpAndPort(target.getHostName(), target.getPort());
  }
}
