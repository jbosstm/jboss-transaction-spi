#!/bin/bash

# Release jboss-transaction-spi to the JBoss Nexus staging repository.
#
# Usage: ./release.sh CURRENT NEXT [REMOTE]
#   CURRENT  release version, e.g. 8.0.1.Final
#   NEXT     next development version (without -SNAPSHOT), e.g. 8.0.2
#   REMOTE   git remote to release from (default: origin)
#
# Two-phase release: this script deploys to Nexus staging; once the staged
# artifacts are verified, run ./post-release.sh to push the commits and tag.

set -e

function fatal {
  echo 1>&2 "Failed: $1"
  exit 1
}

if [ $# -lt 2 ]; then
  echo 1>&2 "usage: $0 CURRENT NEXT [REMOTE]"
  exit 2
fi

CURRENT=$1
NEXT="${2%-SNAPSHOT}-SNAPSHOT"
REMOTE=${3:-origin}
BRANCH=$(git rev-parse --abbrev-ref HEAD)

# A -SNAPSHOT release is never deployed (the release profile skips maven-deploy
# and nxrm3 does not stage snapshots), so reject it before touching the repo.
if [[ $CURRENT == *-SNAPSHOT ]]; then
  echo 1>&2 "$0: CURRENT ('$CURRENT') must be a release version, not a -SNAPSHOT"
  exit 2
fi

# post-release.sh pushes a named branch, so refuse to release from a detached HEAD.
if [ "$BRANCH" == "HEAD" ]; then
  echo 1>&2 "$0: HEAD is detached; check out the release branch before releasing"
  exit 2
fi

echo "Releasing $CURRENT from branch '$BRANCH' (remote '$REMOTE'); next development version will be $NEXT."
read -p "Is ~/.m2/settings.xml configured with server 'jboss-releases-repository' and is your GPG key ready? (y/n) " READY
if [[ $READY != y* ]]; then
  echo "Aborting"
  exit 1
fi

# Refuse to release a dirty working tree.
if [ -n "$(git status --porcelain)" ]; then
  git status
  fatal "working tree is not clean"
fi

# Refuse to overwrite an existing release tag.
if git rev-parse -q --verify "refs/tags/$CURRENT" >/dev/null; then
  fatal "tag '$CURRENT' already exists"
fi

echo "=== Setting version to $CURRENT ==="
mvn versions:set -DnewVersion="$CURRENT" -DgenerateBackupPoms=false || fatal "versions:set $CURRENT"
if [ -n "$(git status --porcelain)" ]; then
  git commit -am "Release $CURRENT" || fatal "commit release version"
fi

echo "=== Building, signing and staging to Nexus ==="
mvn clean deploy -Drelease -DreleaseStaging || fatal "deploy to Nexus staging"

# Tag only after a successful deploy, while HEAD is still the release-version
# commit, so a failed build can be retried without deleting a stale tag.
git tag "$CURRENT" || fatal "tag $CURRENT"

echo "=== Setting version to $NEXT ==="
mvn versions:set -DnewVersion="$NEXT" -DgenerateBackupPoms=false || fatal "versions:set $NEXT"
if [ -n "$(git status --porcelain)" ]; then
  git commit -am "Next is $NEXT" || fatal "commit next version"
fi

echo ""
echo "Done. Verify the staged artifacts in Nexus, then run ./post-release.sh $REMOTE $BRANCH"
