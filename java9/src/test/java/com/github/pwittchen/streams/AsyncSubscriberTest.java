/************************************************************************
 * Licensed under Public Domain (CC0)                                    *
 *                                                                       *
 * To the extent possible under law, the person who associated CC0 with  *
 * this code has waived all copyright and related or neighboring         *
 * rights to this code.                                                  *
 *                                                                       *
 * You should have received a copy of the CC0 legalcode along with this  *
 * work. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.*
 ************************************************************************/

package com.github.pwittchen.streams;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.tck.SubscriberBlackboxVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test // Must be here for TestNG to find and run this, do not remove
public class AsyncSubscriberTest extends SubscriberBlackboxVerification<Integer> {

  private ExecutorService e;

  @BeforeClass void before() {
    e = Executors.newFixedThreadPool(4);
  }

  @AfterClass void after() {
    if (e != null) e.shutdown();
  }

  public AsyncSubscriberTest() {
    super(new TestEnvironment());
  }

  @Override public Subscriber<Integer> createSubscriber() {
    return new AsyncSubscriber<>(e) {
      @Override protected boolean whenNext(final Integer element) {
        return true;
      }
    };
  }

  @Test public void testAccumulation() throws InterruptedException {

    final AtomicLong i = new AtomicLong(Long.MIN_VALUE);
    final CountDownLatch latch = new CountDownLatch(1);
    final Subscriber<Integer> sub = new AsyncSubscriber<>(e) {
      private long acc;

      @Override protected boolean whenNext(final Integer element) {
        acc += element;
        return true;
      }

      @Override protected void whenComplete() {
        i.set(acc);
        latch.countDown();
      }
    };

    new NumberIterablePublisher(0, 10, e).subscribe(sub);
    latch.await(env.defaultTimeoutMillis() * 10, TimeUnit.MILLISECONDS);
    assertEquals(i.get(), 45);
  }

  @Override public Integer createElement(int element) {
    return element;
  }
}