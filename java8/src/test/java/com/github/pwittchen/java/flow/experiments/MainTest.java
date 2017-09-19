package com.github.pwittchen.java.flow.experiments;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
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
  public void shouldAddNumbersToListWithAkkaStreams() {
    // given
    List<Integer> expectedList = Arrays.asList(1, 2, 3, 4, 5);
    List<Integer> actualList = new ArrayList<>();

    // when
    final ActorSystem actorSystem = ActorSystem.create();
    final ActorMaterializer materializer = ActorMaterializer.create(actorSystem);
    final Source<Integer, NotUsed> source = Source.range(1, 5);

    source.runForeach(actualList::add, materializer);

    // then
    assertThat(expectedList).isEqualTo(actualList);
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
    final List<String> list = new ArrayList<>();

    // when

    // with callbacks

    new Thread(() -> ((Callback) () -> {
      list.add("one");
      ((Callback) () -> {
        list.add("two");
        ((Callback) () -> {
          list.add("three");
        }).execute();
      }).execute();
    }).execute()).start();

    final String stringWithCallbacks = list.stream().collect(Collectors.joining(" "));

    // reactive way

    final String stringReactive = Flowable.fromCallable(() -> "one")
        .flatMap(name -> Flowable.fromCallable(() -> name.concat(" two")))
        .flatMap(name -> Flowable.fromCallable(() -> name.concat(" three"))
        ).blockingFirst();

    sleep(3000);

    // then
    assertThat(stringWithCallbacks).isEqualTo(stringReactive);
  }

  @FunctionalInterface
  private interface Callback {
    void execute();
  }

  @Test
  public void shouldPerformStreamOperation() {
    //TODO: add a sample with complicated stream operation - ideally with imperative and reactive way
  }

  @Test
  public void shouldPerformCalculationWithIoScheduler() {
    Observable.range(1, 10)
        .doFinally(this::sleepForAWhile)
        .flatMap(integer -> Observable.just(integer)
            .subscribeOn(Schedulers.io())
            .map(this::simulateIntenseCalculation))
        .compose(applyBenchmarkWithAssertion(4))
        .subscribe(this::printNumberWithThreadInfo);
  }

  @Test
  public void shouldPerformCalculationWithCustomSchedulerInOneThread() {
    final ExecutorService executorService = Executors.newFixedThreadPool(1);
    final Scheduler scheduler = Schedulers.from(executorService);

    Observable.range(1, 10)
        .doFinally(this::sleepForAWhile)
        .flatMap(integer -> Observable.just(integer)
            .subscribeOn(scheduler)
            .map(this::simulateIntenseCalculation))
        .compose(applyBenchmarkWithAssertion(20))
        .subscribe(this::printNumberWithThreadInfo);

    sleepForAWhile();
  }

  @Test
  public void shouldPerformCalculationWithCustomSchedulerWithAllAvailableCores() {
    final int threads = Runtime.getRuntime().availableProcessors();
    final ExecutorService executorService = Executors.newFixedThreadPool(threads);
    final Scheduler scheduler = Schedulers.from(executorService);

    Observable.range(1, 10)
        .doOnSubscribe(disposable -> System.out.println(String.format("using %d threads", threads)))
        .doFinally(this::sleepForAWhile)
        .flatMap(integer -> Observable.just(integer)
            .subscribeOn(scheduler)
            .map(this::simulateIntenseCalculation))
        .compose(applyBenchmarkWithAssertion(8))
        .subscribe(this::printNumberWithThreadInfo);

    sleepForAWhile();
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