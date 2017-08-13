package com.github.pwittchen.java.flow.experiments;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

//TODO: move code below to the separate unit tests
//TODO: create appropriate Gradle tasks to run single unit tests without remembering package name
public class Main {

  public static void main(String args[]) {

    Flux<Integer> flux = Flux.just(1, 2, 3, 4);
    Flowable<Integer> flowable = Flowable.just(5, 6, 7, 8);
    Publisher<Integer> publisher = s -> s.onNext(9);

    flux.mergeWith(flowable).mergeWith(publisher).subscribe(System.out::println);
    System.out.println();
    flowable.mergeWith(flux).sorted().subscribe(System.out::println);

    int threads = Runtime.getRuntime().availableProcessors();
    ExecutorService executorService = Executors.newFixedThreadPool(threads);
    Scheduler scheduler = Schedulers.from(executorService);

    Observable.range(1, 10)
        .doFinally(Main::sleepForAWhile)
        .flatMap(integer -> Observable.just(integer)
            .subscribeOn(Schedulers.computation())
            .map(Main::intenseCalculation))
        //.doOnSubscribe(disposable -> startTime = System.currentTimeMillis())
        //.doOnComplete(() -> {
        //  endTime = System.currentTimeMillis();
        //  long computationTime = (endTime - startTime) / 1000;
        //  System.out.println(String.format("computed in %d seconds", computationTime));
        //})
        .compose(applyBenchmark())
        .subscribe(
            number -> {
              String format =
                  String.format("Received %d %s on thread %s", number, LocalTime.now().toString(),
                      Thread.currentThread().getName());
              System.out.println(format);
            });
  }

  private static <T> ObservableTransformer<T, T> applyBenchmark() {
    // if we want to use local variable for transformer, it needs to be an array
    final long[] executionTime = new long[2];
    return upstream -> upstream
        .doOnSubscribe(disposable -> executionTime[0] = System.currentTimeMillis())
        .doOnComplete(() -> {
          executionTime[1] = System.currentTimeMillis();
          long computationTime = (executionTime[1] - executionTime[0]) / 1000;
          System.out.println(String.format("computed in %d seconds", computationTime));
        });
  }

  private static void sleepForAWhile() {
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static <T> T intenseCalculation(T value) {
    sleep(ThreadLocalRandom.current().nextInt(3000));
    return value;
  }

  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
