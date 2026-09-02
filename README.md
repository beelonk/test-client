# Test Client

Client-side Fabric mod for **Minecraft 1.21**, using **Java 21**,
**Fabric Loader 0.15.11+**, and **Fabric API 0.100.7+1.21**.

## Features and controls

| Default key | Action |
| --- | --- |
| Right Shift | Open module menu with descriptions, toggle buttons and Disable all |
| H | Toggle Info HUD: FPS, coordinates, facing direction and armor durability |
| V | Toggle automatic forward sprint |
| G | Toggle the existing experimental Flight module; double-tap jump to fly |

Rebind these shortcuts under **Options > Controls > Key Binds > Test Client**.
Minecraft saves key bindings in its normal options file. Module enabled states
are session-only; all modules start disabled. Movement modules also turn off
when leaving a world. Shortcuts do not toggle modules while chat or other screens
are open. The HUD respects F1 and hides while the F3 debug overlay is visible.
Armor with 20% or less durability is highlighted red.

Flight is a client-side testing feature, not a server permission grant. Servers
may reject it or disconnect you. Use it only in local test worlds or on servers
where you have permission. Disabling Flight restores captured abilities for
survival players and preserves creative/spectator flight.

## Installation

1. Install Fabric Loader for Minecraft 1.21.
2. Place the matching Fabric API jar and the built `testclient-1.0.0.jar` into
   your Minecraft `mods` folder. Do not install the `-sources.jar`.
3. Launch the Fabric profile, enter a world, and press Right Shift.

## Development

Set `JAVA_HOME` to a Java 21 JDK, then build:

```powershell
git clone https://github.com/beelonk/test-client.git
cd test-client
.\gradlew.bat build
```

On Linux/macOS use `./gradlew build`. The installable jar is written to
`build/libs/testclient-1.0.0.jar`. Launch a development client using
`gradlew.bat runClient` (or `./gradlew runClient`).

### In-game smoke tests

- Open the menu, toggle every module, hover for descriptions, and use Disable all.
- Rebind a toggle (including to a mouse button), restart, and confirm the binding persists.
- Type G, V and H in chat and confirm no modules toggle, including after closing chat.
- Equip damaged armor, enable Info HUD, and check the percentages and warning color.
- Check the HUD at different GUI scales; hide it with F1 and with F3.
- Enable Flight in a survival test world, fly, then disable it and verify normal gravity.
- Disable Flight in creative/spectator and confirm native flying remains available.
- Disconnect with movement modules enabled and confirm they are off on reconnect.

The Gradle build checks compilation and packaging; the interactions above require
a Minecraft client and are not automated tests.
