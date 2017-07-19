package com.github.pwittchen.java.flow.experiments;

import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public class Main {
  public static void main(String args[]) {

    Flux<Integer> flux = Flux.just(1, 2, 3, 4);
    Flowable<Integer> flowable = Flowable.just(5, 6, 7, 8);
    Publisher<Integer> publisher = s -> s.onNext(9);

    flux.mergeWith(flowable).mergeWith(publisher).subscribe(System.out::println);
  }
}
