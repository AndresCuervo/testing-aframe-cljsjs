#!/bin/bash
set -e
lein clean
lein cljsbuild once min

rm -rf docs
mkdir docs
cp -r resources/public/* docs
cp docs/_index.html docs/404.html
mv docs/_index.html docs/index.html
cp target/cljsbuild/public/js/app.js docs/js/app.js
