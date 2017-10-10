package com.github.pwittchen;

import java.util.concurrent.Flow;
import java.util.function.Predicate;
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

    Pipe.create()
        .stream(Stream.of(1, 2, 3, 4, 5, 6))
        .filter(o -> (Integer) o % 2 == 0)
        .subscribe(System.out::println);
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
    subscriber.onComplete();
  }

  void subscribe(final Consumer consumer) {
    subscribe((Flow.Subscriber) consumer);
  }
}