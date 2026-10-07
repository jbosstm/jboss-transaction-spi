#!/bin/bash

# Push the release commits and tag created by ./release.sh.
# Run this only after the staged artifacts have been verified in Nexus.
#
# Usage: ./post-release.sh [REMOTE] [BRANCH]
#   REMOTE  git remote to push to (default: origin)
#   BRANCH  branch to push (default: current branch)

set -e

function fatal {
  echo 1>&2 "Failed: $1"
  exit 1
}

REMOTE=${1:-origin}
BRANCH=${2:-$(git rev-parse --abbrev-ref HEAD)}

echo "This will push branch '$BRANCH' and all tags to remote '$REMOTE'."
read -p "Are you sure you want to continue? (y/n) " PROCEED
if [[ $PROCEED != y* ]]; then
  echo "Aborting"
  exit 1
fi

git push --atomic "$REMOTE" "$BRANCH" --tags || fatal "push to $REMOTE"
