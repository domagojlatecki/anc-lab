#!/bin/bash

args="files/lab1/task2-matrix.txt files/lab1/task2-vector.txt"
kotlin -classpath target/classes at.doml.anc.lab1.MainKt $@ ${args}
