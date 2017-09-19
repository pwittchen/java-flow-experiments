package com.github.pwittchen.java.flow.experiments;

import io.reactivex.Flowable;
import java.util.ArrayList;
import java.util.List;
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
}