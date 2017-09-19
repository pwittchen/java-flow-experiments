package com.github.pwittchen.java.flow.experiments;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.schedulers.Schedulers;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;
import reactor.core.publisher.Flux;

import static com.google.common.truth.Truth.assertThat;

public class MainTest {

  @Test
  public void shouldMergeStreamsFromDifferentApis() {
    // given
    List<Integer> expectedResult = IntStream.range(1, 9).boxed().collect(Collectors.toList());
    List<Integer> actualResult = new ArrayList<>();

    Flowable<Integer> flowable = Flowable.just(1, 2, 3, 4);
    Flux<Integer> flux = Flux.just(5, 6, 7, 8);

    // when
    flowable.mergeWith(flux).sorted().subscribe(actualResult::add);

    // then
    assertThat(actualResult).isEqualTo(expectedResult);
  }

  @Test
  public void shouldPerformCalculationWithComputationScheduler() {
    Observable.range(1, 10)
        .doFinally(this::sleepForAWhile)
        .flatMap(integer -> Observable.just(integer)
            .subscribeOn(Schedulers.computation())
            .map(this::intenseCalculation))
        .compose(applyBenchmarkWithAssertion(8))
        .subscribe(
            number -> {
              String format =
                  String.format("Received %d %s on thread %s", number, LocalTime.now().toString(),
                      Thread.currentThread().getName());
              System.out.println(format);
            });
  }

  private <T> T intenseCalculation(T value) {
    sleep(ThreadLocalRandom.current().nextInt(3000));
    return value;
  }

  private <T> ObservableTransformer<T, T> applyBenchmark() {
    return applyBenchmarkWithAssertion(null);
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