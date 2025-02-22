/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.sparkreceiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.HashSet;
import java.util.Set;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.TestPipelineOptions;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Test class for {@link SparkReceiverIO}. */
@RunWith(JUnit4.class)
public class SparkReceiverIOTest {

  public static final TestPipelineOptions OPTIONS =
      TestPipeline.testingPipelineOptions().as(TestPipelineOptions.class);

  static {
    OPTIONS.setBlockOnRun(false);
  }

  @Rule public final transient TestPipeline pipeline = TestPipeline.fromOptions(OPTIONS);

  @Test
  public void testReadBuildsCorrectly() {
    ReceiverBuilder<String, CustomReceiverWithOffset> receiverBuilder =
        new ReceiverBuilder<>(CustomReceiverWithOffset.class).withConstructorArgs();
    SerializableFunction<String, Long> offsetFn = Long::valueOf;
    SerializableFunction<String, Instant> timestampFn = Instant::parse;

    SparkReceiverIO.Read<String> read =
        SparkReceiverIO.<String>read()
            .withGetOffsetFn(offsetFn)
            .withTimestampFn(timestampFn)
            .withSparkReceiverBuilder(receiverBuilder);

    assertEquals(offsetFn, read.getGetOffsetFn());
    assertEquals(receiverBuilder, read.getSparkReceiverBuilder());
  }

  @Test
  public void testReadObjectCreationFailsIfReceiverBuilderIsNull() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SparkReceiverIO.<String>read().withSparkReceiverBuilder(null));
  }

  @Test
  public void testReadObjectCreationFailsIfGetOffsetFnIsNull() {
    assertThrows(
        IllegalArgumentException.class, () -> SparkReceiverIO.<String>read().withGetOffsetFn(null));
  }

  @Test
  public void testReadObjectCreationFailsIfTimestampFnIsNull() {
    assertThrows(
        IllegalArgumentException.class, () -> SparkReceiverIO.<String>read().withTimestampFn(null));
  }

  @Test
  public void testReadValidationFailsMissingReceiverBuilder() {
    SparkReceiverIO.Read<String> read = SparkReceiverIO.read();
    assertThrows(IllegalStateException.class, read::validateTransform);
  }

  @Test
  public void testReadValidationFailsMissingSparkConsumer() {
    ReceiverBuilder<String, CustomReceiverWithOffset> receiverBuilder =
        new ReceiverBuilder<>(CustomReceiverWithOffset.class).withConstructorArgs();
    SparkReceiverIO.Read<String> read =
        SparkReceiverIO.<String>read().withSparkReceiverBuilder(receiverBuilder);
    assertThrows(IllegalStateException.class, read::validateTransform);
  }

  @Test
  public void testReadFromCustomReceiverWithOffset() {
    CustomReceiverWithOffset.shouldFailInTheMiddle = false;
    ReceiverBuilder<String, CustomReceiverWithOffset> receiverBuilder =
        new ReceiverBuilder<>(CustomReceiverWithOffset.class).withConstructorArgs();
    SparkReceiverIO.Read<String> reader =
        SparkReceiverIO.<String>read()
            .withGetOffsetFn(Long::valueOf)
            .withTimestampFn(Instant::parse)
            .withSparkReceiverBuilder(receiverBuilder);

    for (int i = 0; i < CustomReceiverWithOffset.RECORDS_COUNT; i++) {
      TestOutputDoFn.EXPECTED_RECORDS.add(String.valueOf(i));
    }
    pipeline.apply(reader).setCoder(StringUtf8Coder.of()).apply(ParDo.of(new TestOutputDoFn()));

    pipeline.run().waitUntilFinish(Duration.standardSeconds(15));
  }

  @Test
  public void testReadFromCustomReceiverWithOffsetFailsAndReread() {
    CustomReceiverWithOffset.shouldFailInTheMiddle = true;
    ReceiverBuilder<String, CustomReceiverWithOffset> receiverBuilder =
        new ReceiverBuilder<>(CustomReceiverWithOffset.class).withConstructorArgs();
    SparkReceiverIO.Read<String> reader =
        SparkReceiverIO.<String>read()
            .withGetOffsetFn(Long::valueOf)
            .withTimestampFn(Instant::parse)
            .withSparkReceiverBuilder(receiverBuilder);

    for (int i = 0; i < CustomReceiverWithOffset.RECORDS_COUNT; i++) {
      TestOutputDoFn.EXPECTED_RECORDS.add(String.valueOf(i));
    }
    pipeline.apply(reader).setCoder(StringUtf8Coder.of()).apply(ParDo.of(new TestOutputDoFn()));

    pipeline.run().waitUntilFinish(Duration.standardSeconds(15));

    assertEquals(0, TestOutputDoFn.EXPECTED_RECORDS.size());
  }

  /** {@link DoFn} that throws {@code RuntimeException} if receives unexpected element. */
  private static class TestOutputDoFn extends DoFn<String, String> {
    private static final Set<String> EXPECTED_RECORDS = new HashSet<>();

    @ProcessElement
    public void processElement(@Element String element, OutputReceiver<String> outputReceiver) {
      if (!EXPECTED_RECORDS.contains(element)) {
        throw new RuntimeException("Received unexpected element: " + element);
      } else {
        EXPECTED_RECORDS.remove(element);
        outputReceiver.output(element);
      }
    }
  }
}
