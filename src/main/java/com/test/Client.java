package com.test;

import com.test.module.Mod;
import com.test.module.ModuleManager;
import com.test.ui.ModuleScreen;
import com.test.ui.Hud;
import com.test.render.ChestEspRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Client implements ClientModInitializer {
    private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("testclient", "modules"));

    @Override
    public void onInitializeClient() {
        KeyMapping menu = register("menu", GLFW.GLFW_KEY_RIGHT_SHIFT);
        Map<Mod, KeyMapping> bindings = new LinkedHashMap<>();
        for (Mod module : ModuleManager.INSTANCE.getModules()) {
            bindings.put(module, register(module.getName().toLowerCase(Locale.ROOT), module.getKey()));
        }
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("testclient", "hud"), Hud::render);
        ChestEspRenderer.register();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean inGame = client.player != null && client.level != null;
            while (menu.consumeClick()) {
                if (inGame && client.gui.screen() == null) client.gui.setScreen(new ModuleScreen());
            }
            bindings.forEach((module, binding) -> {
                // Drain all queued input even while a screen is open.
                while (binding.consumeClick()) {
                    if (inGame && client.gui.screen() == null) module.toggle();
                }
            });
            if (!inGame) {
                for (Mod module : ModuleManager.INSTANCE.getModules()) {
                    if (module.getCategory() == Mod.Category.MOVEMENT) module.setEnabled(false);
                    module.onWorldLeave();
                }
            } else if (!client.isPaused()) {
                for (Mod module : ModuleManager.INSTANCE.getEnabledModules()) module.onTick();
            }
        });
    }

    private KeyMapping register(String name, int key) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.testclient." + name, InputConstants.Type.KEYSYM, key, KEY_CATEGORY));
    }
}
