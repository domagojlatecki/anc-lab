#!/bin/bash

args="files/lab1/task6-matrix.txt files/lab1/task6-vector.txt"
kotlin -classpath target/classes at.doml.anc.lab1.MainKt $@ ${args}
