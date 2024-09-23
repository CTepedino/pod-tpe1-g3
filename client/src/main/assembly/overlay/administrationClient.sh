#!/bin/bash

CLASS_PATH="../../../../target/classes"

MAIN_CLASS="ar.edu.itba.pod.client.administrationClient"

java -cp "$CLASS_PATH" $MAIN_CLASS "$@"