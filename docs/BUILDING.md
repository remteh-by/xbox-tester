# Build and signing

Use JDK 17, the committed Gradle 8.9 wrapper and Android SDK Platform 35.
Run `./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug` for local verification.
Debug builds do not need release secrets.

Release builds require `REMTEH_KEYSTORE_FILE`, `REMTEH_KEYSTORE_PASSWORD`,
`REMTEH_KEY_ALIAS` and `REMTEH_KEY_PASSWORD`, supplied as environment variables
or private user Gradle properties. Never place values in this repository or command arguments.
Protect private configuration with owner-only file permissions. The keystore path may be absolute.
Run `./gradlew :app:assembleRelease` in the configured environment; missing credentials fail closed.

Keep the existing application ID and signing certificate for upgrades. Compare the APK certificate
fingerprint with the previous release before distribution. A newly generated key breaks normal upgrades.

The old source contained signing passwords. Removal does not revoke them from Git history.
Review reuse and change affected passwords through the private signing process. No tracked keystore
filenames were found in inspected history on 2026-10-30; this does not prove that the private key
has never been exposed elsewhere. Do not rewrite shared history without a migration plan.
