#!/bin/bash
mvn install -U install -Dmaven.test.skip=true
cd ../ILCDValidationTool/com.okworx.ilcd.validation.tool
mvn package
cd ../../ilcdvalidation
