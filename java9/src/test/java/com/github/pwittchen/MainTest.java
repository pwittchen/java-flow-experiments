package com.github.pwittchen;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class MainTest {

  @Test
  public void shouldTestAddition() {
    assertThat(2 + 2).isEqualTo(4);
  }
}