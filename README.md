<p align="center">
  <img src="assets/Sketchware-Pro.png" style="width: 30%;" />
</p>

# Sketchware CDM

Welcome to Sketchware Pro! Here you'll find the source code of many classes in Sketchware Pro and, most importantly, the place to contribute to Sketchware CDM.

## Building the App
To build the app, you must use Gradle. It's highly recommended to use Android Studio for the best experience.

There are two build variants with different features:

- `minApi26:` This variant supports exporting AABs from projects and compiling Java 1.8, 1.9, 10, and 11 code. However, it only works on Android 8.0 (O) and above.
- `minApi21:` This variant can't produce AABs from projects and can only compile Java 1.7 code, but it supports Android 5 and above.

To select the appropriate build variant in Android Studio, use the Build Variants tab or use the appropriate Gradle build command.

## Contributing

If you'd like to contribute to Sketchware CDM, follow these steps:

1. Fork this repository.
2. Make changes in your forked repository.
3. Test out those changes.
4. Create a pull request in this repository.
5. Your pull request will be reviewed by the repository members and merged if accepted.

We welcome contributions of any size, whether they are major features or bug fixes, but please note that all contributions will be thoroughly reviewed.

### What Changes We're Unlikely to Accept

Most changes related to the user interface (components that already exist in vanilla Sketchware) are unlikely to be accepted. If something design-related gets changed, ideally the whole app should follow the new style too, which is challenging, especially for mods.

### Commit Message

When you make changes to one or more files, you need to commit those changes with a commit message. Here are some guidelines:

- Keep the commit message short and detailed.
- Use one of these commit types as a prefix:
  - `feat:` for a feature, possibly improving something already existing.
  - `fix:` for a fix, such as a bug fix.
  - `style:` for features and updates related to styling.
  - `refactor:` for refactoring a specific section of the codebase.
  - `test:` for everything related to testing.
  - `docs:` for everything related to documentation.
  - `chore:` for code maintenance (you can also use emojis to represent commit types).

Examples:
- `feat: Speed up compiling with new technique`
- `fix: Fix crash during launch on certain phones`
- `refactor: Reformat code in File.java`
