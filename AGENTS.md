# Agent Guidelines

## Local Rules

If `.agents/rules/local.md` (or files within `.agents/rules/local/`) exists,
read and prioritize its instructions as machine- and developer-specific
local guidelines.

## Code Formatting

Any edits to `.kt` or `.kts` files MUST be formatted using "spotless"
in the following situations:

1. Before committing the changes into git
2. At the end of a coding task, before reporting completion to the user.

Spotless formatting is done by running: `./gradlew spotlessApply`

Edits to files with other extensions do NOT require any formatting.
