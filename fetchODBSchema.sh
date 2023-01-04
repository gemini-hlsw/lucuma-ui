#!/bin/bash

# gq https://lucuma-odb-development.herokuapp.com/odb --introspect >templates/src/main/resources/lucuma/schemas/ObservationDB.graphql
# gq https://lucuma-odb-master.herokuapp.com/odb --introspect >templates/src/main/resources/lucuma/schemas/ObservationDB.graphql
gq http://lucuma-postgres-odb-staging.herokuapp.com/odb -H 'Authorization: Bearer 111.4ed718065f21a8f01014c8d08db2c0f74fe3e860ae25bb1f03c5ae4c5e2d0a0e677e6e781a086d0a6638960506cff7db' --introspect >templates/src/main/resources/lucuma/schemas/ObservationDB.graphql
