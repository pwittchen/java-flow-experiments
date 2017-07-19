#!/usr/bin/env bash

export BUILDER_JVM_PATH="/usr/lib/jvm/java-9-oracle"

function showHelp() {
  echo "
        builder.sh is a simple shell script responsible
        for cleaning, building and running this Java 9 application

        Usage:
          -c    cleans the project (removes 'out/' directory)
          -b    builds (compiles) the project
          -r    runs the compiled project
          -h    shows help
        "
}

function clean() {
  rm -rf out/
  echo "project was cleaned"
}

function build() {
  $BUILDER_JVM_PATH/bin/javac -d out/production/java9/ src/com/github/pwittchen/Main.java
  echo "project was built"
}

function run() {
  echo "running the project:"
  $BUILDER_JVM_PATH/bin/java -Dfile.encoding=UTF-8 -classpath out/production/java9 com.github.pwittchen.Main
}

while getopts "hcbr" opt; do
    case "$opt" in
    h)
        showHelp
        exit 0
        ;;
    c)  clean
        ;;
    b)  build
        ;;
    r)  run
        ;;
    esac
done

shift $((OPTIND-1))

[ "$1" = "--" ] && shift
