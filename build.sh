#!/bin/bash
mvn clean package
native-image -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication