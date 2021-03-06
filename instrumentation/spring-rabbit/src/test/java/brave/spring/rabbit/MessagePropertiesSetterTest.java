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
package brave.spring.rabbit;

import brave.propagation.Propagation;
import brave.test.propagation.PropagationSetterTest;
import java.util.Collections;
import org.springframework.amqp.core.MessageProperties;

import static brave.spring.rabbit.SpringRabbitPropagation.GETTER;
import static brave.spring.rabbit.SpringRabbitPropagation.SETTER;

public class MessagePropertiesSetterTest extends PropagationSetterTest<MessageProperties, String> {
  MessageProperties carrier = new MessageProperties();

  @Override public Propagation.KeyFactory<String> keyFactory() {
    return Propagation.KeyFactory.STRING;
  }

  @Override protected MessageProperties carrier() {
    return carrier;
  }

  @Override protected Propagation.Setter<MessageProperties, String> setter() {
    return SETTER;
  }

  @Override protected Iterable<String> read(MessageProperties carrier, String key) {
    String result = GETTER.get(carrier, key);
    return result == null ? Collections.emptyList() : Collections.singleton(result);
  }
}
