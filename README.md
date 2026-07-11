# Physics Mod — NeoForge 26.2 Decompiled

Decompiled and ported source of **Physics Mod 3.1.45** for **NeoForge Minecraft 26.2**. This repository exists for educational and compatibility research — studying how the mod integrates with NeoForge, fixing decompiler artifacts, and validating builds against a current MC snapshot.

> **This is not an official Physics Mod release.** See [Legal notice](#legal-notice) below.

## Legal notice

Physics Mod is original work by **diebuddies** / **Physics Mod Pro** ([minecraftphysicsmod.com](https://minecraftphysicsmod.com/)).

This repository contains **decompiled source** produced for personal compatibility and research work. It is:

- **Not** affiliated with, endorsed by, or maintained by the original authors
- **Not** a substitute for purchasing or licensing the official mod
- Intended for study, debugging, and porting research only

If you use or redistribute this code, respect the original mod''s license and terms of use. Support the official project if you want the maintained, supported product.

## Requirements

| Tool | Version |
|------|---------|
| Java | **25** (Gradle toolchain) |
| NeoForge | **26.2.0.9-beta** (configured in `build.gradle`) |
| Minecraft | **26.2** |

## Build

1. Set up [compile-only dependencies](compile-deps/README.md) (Sodium, Iris, EMF, ETF, Vivecraft JARs).
2. From the project root:

```powershell
.\gradlew.bat build
```

The output JAR is written to `build/libs/`.

## Running

### Gradle dev client

```powershell
.\gradlew.bat runClient
```

This may fail on some setups due to Java toolchain or NeoForge dev-environment quirks. If `runClient` does not start, use an external launcher instead.

### External launcher (tested)

Copy the built JAR from `build/libs/` into a **NeoForge 26.2** instance. This workflow was verified with **GDLauncher**.

## Project layout

```
src/main/java/          Decompiled mod source (net.diebuddies, physx, …)
src/main/resources/     Mixins, access transformers, mod metadata
src/compileStub/java/   Minimal stubs for OptiFine / Vivecraft compile refs
compile-deps/           compileOnly JARs (not committed — see README there)
gradle/                 Gradle wrapper
```

## Known decompiler fixes applied

These changes were required to get a clean compile and working runtime:

### `physx/NativeObject.java` — static init order

The decompiler emitted `SIZEOF_POINTER = __sizeOfPointer()` before native libraries were loaded, causing `UnsatisfiedLinkError` at class init. Fixed by calling `Loader.load()` in a static initializer **before** any native method invocation:

```java
static {
   Loader.load();
}
public static final int SIZEOF_POINTER = __sizeOfPointer();
```

### Mixins — refmap removed

`physicsmod.mixins.json` had a stale `refmap` entry from the original Fabric/Forge build. Removed so NeoForge/Mixin uses runtime discovery against named sources.

### Compile stubs

Optional integration code references OptiFine and Vivecraft APIs. Minimal stub classes under `src/compileStub/java/` satisfy the compiler without bundling those mods.

## Optional dependencies at runtime

Physics Mod integrates with many optional mods (Sodium, Iris, EMF, Vivecraft, etc.). They are not required for a basic build but enable full feature sets when present in the game instance.

## License

The decompiled source retains the original mod''s licensing. This repository adds no separate license beyond documenting the decompile/porting context. Do not treat this as permission to redistribute Physics Mod commercially or as a replacement for the official product.
