#!/usr/bin/env bash

if [ ! -z "$TRAVIS_TAG" ]
then
    echo "on a tag -> set pom.xml <version> to $TRAVIS_TAG"
    mvn --settings .travis/settings.xml org.codehaus.mojo:versions-maven-plugin:2.5:set -DnewVersion=$TRAVIS_TAG -Prelease
else
    echo "not on a tag -> keep snapshot version in pom.xml"
fi

if [ ! -z "$TRAVIS" -a -f "$HOME/.gnupg" ]; then
    shred -v ~/.gnupg/*
    rm -rf ~/.gnupg
fi

source .travis/gpg.sh

mvn clean deploy --settings .travis/settings.xml -DskipTests=true -B -U -Prelease

if [ ! -z "$TRAVIS" ]; then
    shred -v ~/.gnupg/*
    rm -rf ~/.gnupg
fi
