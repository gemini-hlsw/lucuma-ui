#!/bin/bash

#
# fetchODBSchema.sh
#
# Usage: sh fetchODBSchema.sh [local|staging]
#
# Fetches the current version of the ODB schema from either a 'local' ODB
# instance running on localhost, or else from the 'staging' ODB.  Places it
# in the proper place in `lucuma-schemas` for use in generating code.
#

KEY_MESSAGE=$(cat << END
Define the ODB_API_KEY environment variable to use this script. For example:

  export ODB_API_KEY="111.4ed718065f21a8f01014c8d08db2c0f74fe3e860ae25bb1f03c5ae4c5e2d0a0e677e6e781a086d0a6638960506cff7db"

You can get an API key from the Explore "User Preferences" menu item.

END
)

RED='\033[0;31m'
NC='\033[0m'

if [ -z "${ODB_API_KEY}" ]; then
  echo -e "${RED}$KEY_MESSAGE${NC}"
  exit 1
fi

function usage {
  echo -e "${RED}Usage: $0 [local|staging]${NC}"
  exit 1
}

case "$1" in
  local)
    URL="http://localhost:8082/odb"
    ;;
  staging)
    URL="http://lucuma-postgres-odb-staging.herokuapp.com/odb"
    ;;
  *)
    usage
    ;;
esac


# gq https://lucuma-odb-development.herokuapp.com/odb --introspect >templates/src/main/resources/lucuma/schemas/ObservationDB.graphql
# gq https://lucuma-odb-master.herokuapp.com/odb --introspect >templates/src/main/resources/lucuma/schemas/ObservationDB.graphql
gq $URL -H "Authorization: Bearer ${ODB_API_KEY}" --introspect >lucuma-schemas/src/clue/resources/lucuma/schemas/ObservationDB.graphql
