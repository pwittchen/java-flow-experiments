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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
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
  public void shouldAddNumbersToListWithAkkaStream() {
    // given
    List<Integer> expectedList = Arrays.asList(1, 2, 3, 4, 5);
    List<Integer> actualList = new ArrayList<>();

    // when
    final ActorSystem actorSystem = ActorSystem.create();
    final ActorMaterializer materializer = ActorMaterializer.create(actorSystem);
    final Source<Integer, NotUsed> source = Source.range(1, 5);

    source.runForeach(actualList::add, materializer);

    sleep(3000);

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
    final String[] stringWithCallbacks = new String[1];

    // when

    // with callbacks

    new Thread(() -> ((Callback) () -> {
      list.add("one");
      ((Callback) () -> {
        list.add("two");
        ((Callback) () -> {
          list.add("three");
          stringWithCallbacks[0] = list.stream().collect(Collectors.joining(" "));
        }).execute();
      }).execute();
    }).execute()).start();

    // reactive way

    final String stringReactive = Flowable
        .fromCallable(() -> "one")
        .flatMap(name -> Flowable.fromCallable(() -> name.concat(" two")))
        .flatMap(name -> Flowable.fromCallable(() -> name.concat(" three")))
        .blockingFirst();

    sleep(3000);

    // then
    assertThat(stringWithCallbacks[0]).isEqualTo(stringReactive);
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
  public void shouldHandleBackpressure() {
    //TODO: implement
  }

  @Test
  public void shouldHandleErrors() {
    final String message = "Ooops!";

    Flowable<Object> flowableWithErrors = Flowable.fromCallable(() -> {
      throw new RuntimeException(message);
    });

    flowableWithErrors.subscribe(new Subscriber<Object>() {
      @Override public void onSubscribe(Subscription s) {
      }

      @Override public void onNext(Object o) {
      }

      @Override public void onError(Throwable throwable) {
        System.out.println(throwable.getMessage());
        assertThat(throwable.getMessage()).isEqualTo(message);
      }

      @Override public void onComplete() {
      }
    });
  }

  @Test
  public void shouldObserveAndSubscribeOnMainThread() {
    Observable.range(1, 10)
        .map(i -> i * 100)
        .doOnNext(i -> printNumberWithThreadInfo("emitting", i))
        .map(i -> i * 10)
        .subscribe(integer -> printNumberWithThreadInfo("received", integer));

    sleep(3000);
  }

  @Test
  public void shouldSubscribeOnComputationThreadAndReceiveOnNewThread() {
    Observable.range(1, 10)
        .map(i -> i * 100)
        .doOnNext(i -> printNumberWithThreadInfo("emitting", i))
        .map(i -> i * 10)
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.newThread())
        .subscribe(integer -> printNumberWithThreadInfo("received", integer));

    sleep(6000);
  }

  @Test
  public void shouldSubscribeOnComputationThreadAndObserveOnMainThread() {

    final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

    Observable.range(1, 10)
        .map(i -> i * 100)
        .doOnNext(i -> printNumberWithThreadInfo("emitting", i))
        .map(i -> i * 10)
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.from(tasks::add))
        .subscribe(integer -> printNumberWithThreadInfo("received", integer));

    try {
      tasks.take().run();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    sleep(6000);
  }

  @Test
  public void shouldPerformCalculationWithIoScheduler() {
    Observable.range(1, 10)
        .doFinally(this::sleepForAWhile)
        .flatMap(integer -> Observable.just(integer)
            .subscribeOn(Schedulers.io())
            .map(this::simulateIntenseCalculation))
        .compose(applyBenchmarkWithAssertion(4))
        .subscribe(integer -> printNumberWithThreadInfo("received", integer));
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
        .subscribe(integer -> printNumberWithThreadInfo("received", integer));

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
        .subscribe(integer -> printNumberWithThreadInfo("received", integer));

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
        .subscribe(integer -> printNumberWithThreadInfo("received", integer));
  }

  private void printNumberWithThreadInfo(final String message, final Integer number) {
    final String format = message.concat(" %d %s on thread %s");
    final String now = LocalTime.now().toString();
    final String currentThreadName = Thread.currentThread().getName();
    final String formattedMessage = String.format(format, number, now, currentThreadName);
    System.out.println(formattedMessage);
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