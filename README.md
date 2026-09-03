# Test Client

Client-side Fabric mod for **Minecraft Java 26.2**, using **Java 25**,
**Fabric Loader 0.19.3+**, and **Fabric API 0.159.0+26.2**.
Fabric components have their own version numbers; choose the API build marked
`+26.2`. This jar is not compatible with Minecraft 1.21 or 26.1.

## Features and controls

| Default key | Action |
| --- | --- |
| Right Shift | Open module menu with descriptions, toggle buttons and Disable all |
| H | Toggle Info HUD: FPS, coordinates, facing direction and armor durability |
| C | Toggle Chest ESP: through-wall chest highlights within 64 blocks |
| V | Toggle automatic forward sprint |
| G | Toggle the existing experimental Flight module; double-tap jump to fly |

Rebind these shortcuts under **Options > Controls > Key Binds > Test Client**.
Minecraft saves key bindings in its normal options file. Module enabled states
are session-only; all modules start disabled. Movement modules also turn off
when leaving a world. Shortcuts do not toggle modules while chat or other screens
are open. The HUD respects F1 and hides while the F3 debug overlay is visible.
Armor with 20% or less durability is highlighted red.

### Chest ESP

- Gold: normal chests. Red: trapped chests. Purple: ender chests.
- Translucent boxes follow the chest block shapes, including double-chest halves.
- Only already-loaded chests within 64 blocks are considered; at most the nearest
  512 chest blocks are displayed. The cache refreshes every 10 game ticks.
- F1 hides ESP too. Disabling the module clears its cache, and changing dimensions
  cannot reuse chest positions from the previous world.
- Uses Minecraft's Blaze3D rendering pipeline, without raw OpenGL calls.
- Does not inspect container contents, send discovery packets, or load chunks.

Use ESP only in your own worlds or where server rules permit it.

Flight is a client-side testing feature, not a server permission grant. Servers
may reject it or disconnect you. Use it only in local test worlds or on servers
where you have permission. Disabling Flight restores captured abilities for
survival players and preserves creative/spectator flight.

## Installation

1. Install Fabric Loader for Minecraft 26.2 and use Java 25.
2. Replace the old Test Client jar with `testclient-1.1.0+26.2.jar`, and place
   Fabric API `0.159.0+26.2` alongside it
   your Minecraft `mods` folder. Do not install the `-sources.jar`.
3. Launch the Fabric profile, enter a world, and press Right Shift.

## Development

Set `JAVA_HOME` to a Java 25 JDK, then build with the included Gradle 9.5.1 wrapper:

```powershell
git clone https://github.com/beelonk/test-client.git
cd test-client
.\gradlew.bat build
```

On Linux/macOS use `./gradlew build`. The installable jar is written to
`build/libs/testclient-1.1.0+26.2.jar`. Launch a development client using
`gradlew.bat runClient` (or `./gradlew runClient`).

### Automated client smoke test

Run `gradlew.bat runClientGameTest` (or `./gradlew runClientGameTest`) on a machine
with graphics support. It creates a disposable world, places normal/trapped/ender
chests behind a stone wall, checks detection and removal, exercises the menu's
Disable all button, and captures enabled/disabled screenshots. The test mod is
separate and is not included in the installable jar.

### Additional in-game checks

- Open the menu, toggle every module, hover for descriptions, and use Disable all.
- Rebind a toggle (including to a mouse button), restart, and confirm the binding persists.
- Type G, V, H and C in chat and confirm no modules toggle, including after closing chat.
- Equip damaged armor, enable Info HUD, and check the percentages and warning color.
- Check the HUD at different GUI scales; hide it with F1 and with F3.
- Enable Flight in a survival test world, fly, then disable it and verify normal gravity.
- Disable Flight in creative/spectator and confirm native flying remains available.
- Disconnect with movement modules enabled and confirm they are off on reconnect.
- Check ESP for double chests, chunk unloads, dimension changes, and both graphics backends.

The normal Gradle build checks compilation and packaging. Run the client smoke
test separately; the additional checks above still require manual verification.
