#!/bin/sh

echo "Check that we have REACT_APP_BASE_URL vars"
test -n "$REACT_APP_BASE_URL"

find /dist \( -type d -name .git -prune \) -o -type f -print0 | xargs -0 sed -i "s#PH_REACT_APP_BASE_URL#$REACT_APP_BASE_URL#g"

echo "Starting React"
exec "$@"