#!/bin/bash

###
# This script exports the Lider Console product for Linux (x32, x64), Windows (x32, x64) and MacOSX.
#
# Exported products can be found under tr.org.pardus.lider.console.products/target/products directory.
###

# Generate third-party dependencies
cd ../lider-ahenk-installer-dependencies
mvn clean p2:site

# Start jetty server for Tycho to use generated dependencies
mvn jetty:run &
J_PID=$!

# While server is running, start Tycho
cd ..
mvn clean verify

# After exporting products, kill jetty server process
kill $J_PID
