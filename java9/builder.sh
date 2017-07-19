#!/usr/bin/env bash

export BUILDER_JVM_PATH="/usr/lib/jvm/java-9-oracle"
export BUILDER_OUT_DIR="out/production/java9/"
export BUILDER_SRC_FILE="src/com/github/pwittchen/Main.java"
export BUILDER_MAIN_CLASS="com.github.pwittchen.Main"

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
  rm -rf $BUILDER_OUT_DIR
  echo "project was cleaned"
}

function build() {
  $BUILDER_JVM_PATH/bin/javac -d $BUILDER_OUT_DIR $BUILDER_SRC_FILE
  echo "project was built"
}

function run() {
  echo "running the project:"
  $BUILDER_JVM_PATH/bin/java -Dfile.encoding=UTF-8 -classpath $BUILDER_OUT_DIR $BUILDER_MAIN_CLASS
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
