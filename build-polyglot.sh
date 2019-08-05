#!/bin/bash
mvn clean package
native-image -cp target/classes:target/dependency/* --language:python --language:js --language:ruby --language:R com.kumuluz.ee.EeApplication