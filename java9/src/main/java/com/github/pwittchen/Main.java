package com.github.pwittchen;

import com.github.pwittchen.streams.NumberIterablePublisher;
import com.github.pwittchen.streams.SyncSubscriber;

public class Main {
  public static void main(final String[] args) {

    new NumberIterablePublisher(1, 11, Runnable::run)
        .subscribe(new SyncSubscriber<>() {
          @Override protected boolean whenNext(Integer element) {
            System.out.println(element);
            return true;
          }
        });
  }
}

