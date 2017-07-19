# java-flow-experiments
In this repository, I'm experimenting with [Reactive Streams](http://www.reactive-streams.org), which are going to become part of the Java 9 in the `java.util.concurrent` package.

Inside `java8` directory, I'm playing with two implementations of Reactive Streams:
- [Project Reactor](https://projectreactor.io/) (it's going to be included in Spring 5)
- [RxJava2](https://github.com/ReactiveX/RxJava)

In addition, inside `java9` directory I'm playing with native Java 9 Reactive Streams interfaces.
Please note, in this setup, `java9/build.sh` script may not work in your environment. You can import this project to IntelliJ IDEA, set Java 9 as default JDK and it should work. I haven't used Gradle for Java 9 because Gradle support for Java9 is still in progress right now.
