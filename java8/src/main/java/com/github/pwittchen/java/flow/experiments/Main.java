package com.github.pwittchen.java.flow.experiments;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

//TODO: move code below to the separate unit tests
//TODO: create appropriate Gradle tasks to run single unit tests without remembering package name
public class Main {

  private static long startTime;
  private static long endTime;

  public static void main(String args[]) {

    Flux<Integer> flux = Flux.just(1, 2, 3, 4);
    Flowable<Integer> flowable = Flowable.just(5, 6, 7, 8);
    Publisher<Integer> publisher = s -> s.onNext(9);

    flux.mergeWith(flowable).mergeWith(publisher).subscribe(System.out::println);

    System.out.println();

    flowable.mergeWith(flux).sorted().subscribe(System.out::println);

    Observable.range(1, 10)
        .doFinally(Main::sleepForAWhile)
        .doOnSubscribe(disposable -> startTime = System.currentTimeMillis())
        .flatMap(integer -> Observable.just(integer)
            .subscribeOn(Schedulers.computation())
            .map(Main::intenseCalculation))
        .doOnComplete(() -> {
          endTime = System.currentTimeMillis();
          long computationTime = (endTime - startTime) / 1000;
          System.out.println(String.format("computed in %d seconds", computationTime));
        })
        .subscribe(
            number -> {
              String format =
                  String.format("Received %d %s on thread %s", number, LocalTime.now().toString(),
                      Thread.currentThread().getName());
              System.out.println(format);
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
