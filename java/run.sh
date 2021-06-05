#!/bin/bash

export CLASSPATH=jar/annotations-4.1.1.4.jar:jar/annotations-api-6.0.53.jar:jar/commons-logging-api-1.1.jar:jar/google-collections-1.0.jar:jar/grpc-api-1.38.0.jar:jar/grpc-context-1.38.0.jar:jar/grpc-core-1.38.0.jar:jar/grpc-netty-shaded-1.38.0.jar:jar/grpc-protobuf-1.38.0.jar:jar/grpc-protobuf-lite-1.38.0.jar:jar/grpc-stub-1.38.0.jar:jar/gson-2.8.6.jar:jar/guava-28.0-jre.jar:jar/javax.inject-1.jar:jar/jsr250-api-1.0.jar:jar/jsr305-2.0.1.jar:jar/jsr305-3.0.2.jar:jar/junit-3.8.2.jar:jar/log4j-1.2.12.jar:jar/proto-google-common-protos-2.0.1.jar:jar/protobuf-java-3.12.0.jar:jar/httpcore-4.4.14.jar:src/main/java

javac src/main/java/example/WavToSpeakerGrpc.java
java example.WavToSpeakerGrpc ../output.wav
