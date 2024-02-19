# lucuma-schemas

The purpose of this project is to emit an artifact that:

- Includes the schema files, so that it can be referenced by projects downstream to validate and generate queries.
- Includes the types generated from those schemas.
- Is cross compiled for the JVM and JS.

## Structure

### Project `templates`

Contains:

- Schema files in `resources` (eg: `ObservationDB.graphql`). They can be updated with `fetchODBSchema.mjs`. Use the `flake.nix` with `nix develop`, and run `npm install` to use.
- Templates (eg: `ObservationDB.scala`) to map schema scalars and other types to Scala types.

### Project `lucuma-schemas`

This is the project that is published.

Contains:

- Full-fledged generated schema source (using `clue`'s generator), as managed source.
- Utilities, encoders, decoders, etc. to work with the schema.

Also adds `templates`'s resources as this project's resources, so that schema files are included in the published artifacts.

## Usage

In downstream projects using `clue`, to generate code for queries with `scalafix GraphQLGen`, the following settings are needed:

- In `build.sbt`:

```scala
  scalafixDependencies += "edu.gemini" %% "lucuma-schemas" % lucumaSchemasVersion,
  libraryDependencies += "edu.gemini" %% "lucuma-schemas" % lucumaSchemasVersion,
```

The first line makes the schema file available to the scalafix rule. The second one adds the classes from this project as a regular runtime dependency.

- In `.scalafix.conf`:

```
  GraphQLGen.schemaDirs=["/lucuma/schemas"]
```
