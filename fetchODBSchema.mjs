#!/usr/bin/env node
import { writeFile } from 'fs/promises';
import { buildClientSchema, getIntrospectionQuery, printSchema } from 'graphql';

//
// fetchODBSchema.mjs
//
// Usage: fetchODBSchema.mjs [local|dev|staging]
//
// Fetches the current version of the ODB schema from either a 'local' ODB
// instance running on localhost, or else from the 'dev' or 'staging' ODB.  Places it
// in the proper place in `lucuma-schemas` for use in generating code.
//

const KEY_MESSAGE = `
Define the ODB_API_KEY environment variable to use this script. For example:

  export ODB_API_KEY="111.4ed718065f21a8f01014c8d08db2c0f74fe3e860ae25bb1f03c5ae4c5e2d0a0e677e6e781a086d0a6638960506cff7db"

You can get an API key from the Explore "User Preferences" menu item in Explore.`;

const RED = '\x1b[0;31m';
const NC = '\x1b[0m';

if (!process.env.ODB_API_KEY) {
  console.error(`${RED}${KEY_MESSAGE}${NC}`);
  process.exit(1);
}

function usage() {
  console.error(`${RED}Usage: ${process.argv[1]} [local|dev|staging]${NC}`);
  process.exit(1);
}

let url;

// [2] is the first arg (after node and the script name)
switch (process.argv[2]) {
  case 'local':
    url = 'http://localhost:8082/odb';
    break;
  case 'dev':
    url = 'https://lucuma-postgres-odb-dev.herokuapp.com/odb';
    break;
  case 'staging':
    url = 'https://lucuma-postgres-odb-staging.herokuapp.com/odb';
    break;

  default:
    usage();
    break;
}

const response = await fetch(new URL(url), {
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${process.env.ODB_API_KEY}`,
  },
  method: 'POST',
  body: JSON.stringify({ query: getIntrospectionQuery() }),
});

if (!response.ok) {
  throw new Error(
    `Failed to fetch introspection query: ${response.statusText}`
  );
}

console.log('Fetched ODB schema.');

const data = (await response.json()).data;

const schema = printSchema(buildClientSchema(data));

await writeFile(
  'lucuma-schemas/src/clue/resources/lucuma/schemas/ObservationDB.graphql',
  schema
);

console.log('Wrote ODB schema to lucuma-schemas.');
