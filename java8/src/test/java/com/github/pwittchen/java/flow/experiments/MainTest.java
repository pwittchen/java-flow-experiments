package com.github.pwittchen.java.flow.experiments;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.schedulers.Schedulers;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;
import reactor.core.publisher.Flux;

import static com.google.common.truth.Truth.assertThat;

public class MainTest {
  @Test
  public void shouldMergeStreamsFromDifferentApis() {
    // given
    List<Integer> expectedResult = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
    List<Integer> actualResult = new ArrayList<>();

    Flowable<Integer> flowable = Flowable.just(1, 2, 3, 4);   // RxJava2
    Flux<Integer> flux = Flux.just(5, 6, 7, 8);               // Project Reactor

    // when
    flowable.mergeWith(flux).sorted().subscribe(actualResult::add);

    // then
    assertThat(actualResult).isEqualTo(expectedResult);
  }

  @Test
  public void shouldDoTheSameJobAsImperativeCodeInReactiveWay() {
    // given
    final List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
    final List<Integer> evenNumbersImperative = new ArrayList<>();
    final List<Integer> evenNumbersReactive = new ArrayList<>();

    // when

    // imperative way
    for (Integer n : list) {
      if (n % 2 == 0) {
        evenNumbersImperative.add(n);
      }
    }

    // reactive way
    Flowable.fromIterable(list)
        .filter(n -> n % 2 == 0)
        .subscribe(evenNumbersReactive::add);

    // then
    assertThat(evenNumbersImperative).isEqualTo(evenNumbersReactive);
  }

  @Test
  public void shouldPerformOneOperationAfterAnother() {
    // given
    final List<String> operationsWithCallbacks = new ArrayList<>();
    final List<String> operationsReactive = new ArrayList<>();

    // when

    // with callbacks

    new Thread(() -> ((Callback) () -> {
      operationsWithCallbacks.add("operation 1");
      ((Callback) () -> {
        operationsWithCallbacks.add("operation 2");
        ((Callback) () -> {
          operationsWithCallbacks.add("operation 3");
        }).execute();
      }).execute();
    }).execute()).start();

    // reactive way

    final Flowable<String> first = Flowable.fromCallable(() -> "operation 1");
    final Flowable<String> second = Flowable.fromCallable(() -> "operation 2");
    final Flowable<String> third = Flowable.fromCallable(() -> "operation 3");

    first.flatMap(name -> {
      operationsReactive.add(name);
      return second;
    }).flatMap(name -> {
      operationsReactive.add(name);
      return third;
    }).subscribe(operationsReactive::add);

    assertThat(operationsWithCallbacks).isEqualTo(operationsReactive);
  }

  @FunctionalInterface
  private interface Callback {
    void execute();
  }

  @Test
  public void shouldPerformCalculationWithIoScheduler() {
    //TODO: implement
  }

  @Test
  public void shouldPerformCalculationWithCustomSchedulerOnOneThread() {
    //TODO: implement
  }

  @Test
  public void shouldPerformCalculationWithCustomSchedulerWithAllAvailableCores() {
    //TODO: implement
  }

  @Test
  public void shouldPerformCalculationWithComputationScheduler() {
    Observable.range(1, 10)
        .doFinally(this::sleepForAWhile)
        .flatMap(integer -> Observable.just(integer)
            .subscribeOn(Schedulers.computation())
            .map(this::simulateIntenseCalculation))
        .compose(applyBenchmarkWithAssertion(8))
        .subscribe(this::printNumberWithThreadInfo);
  }

  private void printNumberWithThreadInfo(Integer number) {
    final String format = "Received %d %s on thread %s";
    final String now = LocalTime.now().toString();
    final String currentThreadName = Thread.currentThread().getName();
    final String message = String.format(format, number, now, currentThreadName);
    System.out.println(message);
  }

  private <T> T simulateIntenseCalculation(T value) {
    sleep(ThreadLocalRandom.current().nextInt(3000));
    return value;
  }

  private <T> ObservableTransformer<T, T> applyBenchmarkWithAssertion(
      final Integer maxExpectedComputationTime) {
    // if we want to use local variable for transformer, it needs to be an array
    final long[] executionTime = new long[2];
    return upstream -> upstream
        .doOnSubscribe(disposable -> executionTime[0] = System.currentTimeMillis())
        .doOnComplete(() -> {
          executionTime[1] = System.currentTimeMillis();
          long computationTime = (executionTime[1] - executionTime[0]) / 1000;
          System.out.println(String.format("computed in %d seconds", computationTime));
          if (maxExpectedComputationTime != null) {
            assertThat(computationTime).isLessThan(maxExpectedComputationTime.longValue());
          }
        });
  }

  private void sleepForAWhile() {
    sleep(10000);
  }

  private void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}