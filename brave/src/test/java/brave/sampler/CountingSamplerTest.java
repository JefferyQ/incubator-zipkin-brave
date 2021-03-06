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
package brave.sampler;

import org.assertj.core.data.Percentage;
import org.junit.Test;

import static org.assertj.core.data.Percentage.withPercentage;

public class CountingSamplerTest extends SamplerTest {
  @Override Sampler newSampler(float rate) {
    return CountingSampler.create(rate);
  }

  @Override Percentage expectedErrorRate() {
    return withPercentage(0);
  }

  @Test
  public void sampleRateMinimumOnePercent() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    newSampler(0.0001f);
  }
}
