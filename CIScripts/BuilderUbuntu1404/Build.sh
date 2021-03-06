#!/bin/bash
set -e

# We don't use CONTROLLER_BRANCH and GENERATEDS_BRANCH because we only build vRouter for now

if [ "$#" -ne 5 ]; then
    echo "Usage: TOOLS_BRANCH CONTROLLER_BRANCH VROUTER_BRANCH GENERATEDS_BRANCH SANDESH_BRANCH"
    exit 1
fi

mkdir -p contrail-dev
pushd contrail-dev
    ~/bin/repo init -u git@github.com:codilime/contrail-vnc.git -m vrouter-manifest.xml -b windows
    ~/bin/repo sync
popd

cd contrail-dev
pushd tools
  pushd build
    git checkout $1 --
  popd
  pushd sandesh
    git checkout $5 --
  popd
popd
pushd vrouter
  git checkout $3 --
popd
scons vrouter
