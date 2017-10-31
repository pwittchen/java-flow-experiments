package com.github.pwittchen.java.flow.experiments;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;

public class MainTest {

  @Test
  public void shouldPerformCalculationWithCustomSchedulerInOneThread() {
    final ExecutorService executorService = Executors.newFixedThreadPool(1);
    final Scheduler scheduler = Schedulers.from(executorService);

    Observable.range(1, 10)
        .doFinally(this::sleepForAWhile)
        .flatMap(integer -> Observable.just(integer)
            .subscribeOn(scheduler)
            .map(this::simulateIntenseCalculation))
        .compose(applyBenchmark())
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
        .compose(applyBenchmark())
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
        .compose(applyBenchmark())
        .subscribe(this::printNumberWithThreadInfo);
  }

  private void printNumberWithThreadInfo(final Integer number) {
    final String format = "received".concat(" %d %s on thread %s");
    final String now = LocalTime.now().toString();
    final String currentThreadName = Thread.currentThread().getName();
    final String formattedMessage = String.format(format, number, now, currentThreadName);
    System.out.println(formattedMessage);
  }

  private <T> T simulateIntenseCalculation(final T value) {
    sleep(ThreadLocalRandom.current().nextInt(3000));
    return value;
  }

  private <T> ObservableTransformer<T, T> applyBenchmark() {
    // if we want to use local variable for transformer, it needs to be an array
    final long[] executionTime = new long[2];
    return upstream -> upstream
        .doOnSubscribe(disposable -> executionTime[0] = System.currentTimeMillis())
        .doOnComplete(() -> {
          executionTime[1] = System.currentTimeMillis();
          final long computationTime = (executionTime[1] - executionTime[0]) / 1000;
          System.out.println(String.format("computed in %d seconds", computationTime));
        });
  }

  private void sleepForAWhile() {
    sleep(10000);
  }

  private void sleep(final long millis) {
    try {
      Thread.sleep(millis);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
  }
}