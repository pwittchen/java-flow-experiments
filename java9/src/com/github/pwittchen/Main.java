package com.github.pwittchen;

import java.util.concurrent.Flow;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@FunctionalInterface interface Consumer extends Flow.Subscriber {
  default void onSubscribe(final Flow.Subscription subscription) {
  }

  @Override void onNext(Object item);

  default void onError(final Throwable throwable) {
    throwable.printStackTrace();
  }

  default void onComplete() {
  }
}

public class Main {
  public static void main(final String[] args) {

    final Flow.Publisher<Integer> publisher = subscriber -> {
      IntStream.range(1, 11).forEach(subscriber::onNext);
      subscriber.onComplete();
    };

    publisher.subscribe(new Flow.Subscriber<>() {
      @Override public void onSubscribe(final Flow.Subscription subscription) {
        System.out.println("onSubscribe");
      }

      @Override public void onNext(final Integer item) {
        System.out.println("onNext: ".concat(item.toString()));
      }

      @Override public void onError(final Throwable throwable) {
        System.out.println("onError: ".concat(throwable.getMessage()));
      }

      @Override public void onComplete() {
        System.out.println("onComplete");
      }
    });

    Pipe.create()
        .stream(Stream.of(1, 2, 3, 4, 5, 6))
        .filter(o -> (Integer) o % 2 == 0)
        .subscribe((Consumer) System.out::println);
  }
}

class Pipe implements Flow.Publisher {
  private Stream stream;

  static Pipe create() {
    return new Pipe();
  }

  Pipe stream(final Stream stream) {
    this.stream = stream;
    return this;
  }

  Pipe filter(final Predicate predicate) {
    stream = stream.filter(predicate);
    return this;
  }

  @Override public void subscribe(final Flow.Subscriber subscriber) {
    stream.forEach(subscriber::onNext);
  }
}