#!/bin/sh

# Temporary script to install CookieCore until we find a repo for it
# THIS SHOULD BE REMOVED EVENTUALLY.

gradledir=$("pwd")
cd ..
rm -rf CookieCoreGit
git clone https://github.com/Ephys/CookieCore.git CookieCoreGit
cd CookieCoreGit || exit 1

"${gradledir}/gradlew" install || echo "!! FAILED !!"
