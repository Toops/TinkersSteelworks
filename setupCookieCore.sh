
# Temporary script to install CookieCore until we find a repo for it
# THIS SHOULD BE REMOVED EVENTUALLY.

TSDIR="$(pwd)"
cd ..
git clone https://github.com/Ephys/CookieCore.git CookieCore
cd CookieCore || exit 1

$TSDIR/gradlew install || echo "!! FAILED !!"
