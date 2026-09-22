#!/bin/bash

FILES=packages/*.*

for f in $FILES
do
  echo "Processing file $f..."
  caffeinate curl \
    --url "https://api.bitbucket.org/2.0/repositories/okusche/ilcdvalidationtool/downloads" \
    --header "Authorization: Bearer $(cat ./api-token.txt)" \
    -F files=@$f
done


