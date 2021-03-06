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
package brave.internal.propagation;

import brave.internal.Nullable;
import brave.propagation.CurrentTraceContext.Scope;
import brave.propagation.CurrentTraceContext.ScopeDecorator;
import brave.propagation.TraceContext;

/**
 * Adds correlation properties "traceId", "parentId" and "spanId" when a {@link
 * brave.Tracer#currentSpan() span is current}.
 */
public abstract class CorrelationFieldScopeDecorator implements ScopeDecorator {

  /**
   * When the input is not null "traceId", "parentId" and "spanId" correlation properties are saved
   * off and replaced with those of the current span. When the input is null, these properties are
   * removed. Either way, "traceId", "parentId" and "spanId" properties are restored on {@linkplain
   * Scope#close()}.
   */
  @Override public Scope decorateScope(@Nullable TraceContext currentSpan, Scope scope) {
    String previousTraceId = get("traceId");
    String previousSpanId = get("spanId");
    String previousParentId = get("parentId");

    if (currentSpan != null) {
      maybeReplaceTraceContext(currentSpan, previousTraceId, previousParentId, previousSpanId);
    } else {
      remove("traceId");
      remove("parentId");
      remove("spanId");
    }

    class CorrelationFieldCurrentTraceContextScope implements Scope {
      @Override public void close() {
        scope.close();
        replace("traceId", previousTraceId);
        replace("parentId", previousParentId);
        replace("spanId", previousSpanId);
      }
    }
    return new CorrelationFieldCurrentTraceContextScope();
  }

  /**
   * Idempotently sets correlation properties to hex representation of trace identifiers in this
   * context.
   */
  void maybeReplaceTraceContext(
      TraceContext currentSpan,
      String previousTraceId,
      @Nullable String previousParentId,
      String previousSpanId
  ) {
    String traceId = currentSpan.traceIdString();
    if (!traceId.equals(previousTraceId)) put("traceId", currentSpan.traceIdString());

    String parentId = currentSpan.parentIdString();
    if (parentId == null) {
      remove("parentId");
    } else {
      boolean sameParentId = parentId.equals(previousParentId);
      if (!sameParentId) put("parentId", parentId);
    }

    String spanId = currentSpan.spanIdString();
    if (!spanId.equals(previousSpanId)) put("spanId", spanId);
  }

  /**
   * Returns the correlation property of the specified name iff it is a string, or null otherwise.
   */
  protected abstract @Nullable String get(String key);

  /** Replaces the correlation property of the specified name */
  protected abstract void put(String key, String value);

  /** Removes the correlation property of the specified name */
  protected abstract void remove(String key);

  final void replace(String key, @Nullable String value) {
    if (value != null) {
      put(key, value);
    } else {
      remove(key);
    }
  }
}
