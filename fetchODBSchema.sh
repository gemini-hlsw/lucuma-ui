#!/bin/bash

# gq https://lucuma-odb-development.herokuapp.com/odb --introspect >templates/src/main/resources/lucuma/schemas/ObservationDB.graphql
# gq http://localhost:8083/odb --introspect >templates/src/main/resources/lucuma/schemas/ObservationDB.graphql
gq https://lucuma-odb-master.herokuapp.com/odb --introspect >templates/src/main/resources/lucuma/schemas/ObservationDB.graphql
