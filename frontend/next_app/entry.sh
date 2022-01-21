#!/bin/sh

echo "Check that we have REACT_APP_BASE_URL,PH_GOOGLE_CLIENT_ID,PH_GOOGLE_CLIENT_SECRET vars"
test -n "$REACT_APP_BASE_URL"
test -n "$PH_GOOGLE_CLIENT_ID"
test -n "$PH_GOOGLE_CLIENT_SECRET"

find prodBuild/nextBuild \( -type d -name .git -prune \) -o -type f -print0 | xargs -0 sed -i "s#PH_NEXT_PUBLIC_REACT_APP_BASE_URL#$NEXT_PUBLIC_REACT_APP_BASE_URL#g"
find prodBuild/nextBuild \( -type d -name .git -prune \) -o -type f -print0 | xargs -0 sed -i "s#PH_GOOGLE_CLIENT_ID#$GOOGLE_CLIENT_ID#g"
find prodBuild/nextBuild \( -type d -name .git -prune \) -o -type f -print0 | xargs -0 sed -i "s#PH_GOOGLE_CLIENT_SECRET#$GOOGLE_CLIENT_SECRET#g"

echo "Starting React"
exec "$@"