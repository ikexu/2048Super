#!/usr/bin/env bash
cd ../assembly
mvn clean -Dmaven.test.skip=true compile package