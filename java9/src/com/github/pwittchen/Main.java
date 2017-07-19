package com.github.pwittchen;

import java.util.concurrent.Flow;

public class Main {
  public static void main(String[] args) {

    Flow.Publisher<Integer> publisher = subscriber -> {
      subscriber.onNext(7);
      subscriber.onComplete();
    };

    publisher.subscribe(new Flow.Subscriber<>() {
      @Override public void onSubscribe(Flow.Subscription subscription) {
        System.out.println("onSubscribe");
      }

      @Override public void onNext(Integer item) {
        System.out.println("onNext: ".concat(item.toString()));
      }

      @Override public void onError(Throwable throwable) {
        System.out.println("onError: ".concat(throwable.getMessage()));
      }

      @Override public void onComplete() {
        System.out.println("onComplete");
      }
    });
  }
}
