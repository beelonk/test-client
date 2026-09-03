package com.test;

import com.test.module.Mod;
import com.test.module.ModuleManager;
import com.test.module.render.ChestEsp;
import com.test.ui.ModuleScreen;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.core.BlockPos;
import org.lwjgl.glfw.GLFW;

/** Runs only in the separate test mod, never in the shipped client. */
public class ChestEspGameTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        try (var world = context.worldBuilder().create()) {
            var server = world.getServer();
            server.runCommand("gamemode creative @a");
            server.runCommand("fill -4 99 -4 4 105 10 air");
            server.runCommand("fill -4 99 -4 4 99 10 stone");
            server.runCommand("tp @a 0.5 100 0.5 0 0");
            server.runCommand("setblock -2 100 6 chest");
            server.runCommand("setblock 0 100 6 trapped_chest");
            server.runCommand("setblock 2 100 6 ender_chest");
            server.runCommand("setblock 70 100 0 chest");
            server.runCommand("fill -4 100 4 4 104 4 stone");
            world.getConnection().waitForChunksRender();
            context.getInput().lookAt(new BlockPos(0, 101, 6));
            context.getInput().pressKey(GLFW.GLFW_KEY_C);
            context.waitFor(mc -> chestEsp().positions(mc.level).size() == 3);
            context.waitTicks(20);
            context.takeScreenshot("chest-esp-through-wall");

            context.getInput().pressKey(GLFW.GLFW_KEY_H);
            context.waitTicks(5);
            context.takeScreenshot("info-hud-26-2");
            context.getInput().pressKey(GLFW.GLFW_KEY_F1);
            context.waitTicks(5);
            context.takeScreenshot("hidden-hud-and-esp");
            context.getInput().pressKey(GLFW.GLFW_KEY_F1);

            context.getInput().pressKey(GLFW.GLFW_KEY_T);
            context.waitTicks(3);
            context.getInput().typeChars("cghv");
            context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
            context.waitTicks(3);
            context.runOnClient(mc -> {
                if (!chestEsp().isEnabled() || ModuleManager.INSTANCE.getEnabledModules().size() != 2) {
                    throw new AssertionError("Typing in chat toggled a module");
                }
            });

            // Opening the actual menu also exercises its migrated 26.2 widgets.
            context.getInput().pressKey(GLFW.GLFW_KEY_RIGHT_SHIFT);
            context.waitForScreen(ModuleScreen.class);
            context.takeScreenshot("module-menu-26-2");
            context.clickScreenButton("Disable all");
            context.runOnClient(mc -> {
                if (!ModuleManager.INSTANCE.getEnabledModules().isEmpty()) {
                    throw new AssertionError("Disable all left modules enabled");
                }
                if (!chestEsp().positions(mc.level).isEmpty()) {
                    throw new AssertionError("Disabled ESP retained visible positions");
                }
            });
            context.clickScreenButton("Done");
            context.waitTicks(5);
            context.takeScreenshot("chest-esp-disabled");

            context.getInput().pressKey(GLFW.GLFW_KEY_C);
            context.waitFor(mc -> chestEsp().positions(mc.level).size() == 3);
            server.runCommand("setblock -2 100 6 air");
            context.waitFor(mc -> chestEsp().positions(mc.level).size() == 2);
            context.runOnClient(mc -> {
                for (Mod module : ModuleManager.INSTANCE.getModules()) module.setEnabled(false);
            });
        }
    }

    private static ChestEsp chestEsp() {
        return ModuleManager.INSTANCE.getModules().stream().filter(ChestEsp.class::isInstance)
                .map(ChestEsp.class::cast).findFirst().orElseThrow();
    }
}
