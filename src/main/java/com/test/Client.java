package com.test;

import com.test.module.Mod;
import com.test.module.ModuleManager;
import com.test.ui.ModuleScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Client implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyBinding menu = register("menu", GLFW.GLFW_KEY_RIGHT_SHIFT);
        Map<Mod, KeyBinding> bindings = new LinkedHashMap<>();
        for (Mod module : ModuleManager.INSTANCE.getModules()) {
            bindings.put(module, register(module.getName().toLowerCase(Locale.ROOT), module.getKey()));
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean inGame = client.player != null && client.world != null;
            while (menu.wasPressed()) {
                if (inGame && client.currentScreen == null) client.setScreen(new ModuleScreen());
            }
            bindings.forEach((module, binding) -> {
                // Drain all queued input even while a screen is open.
                while (binding.wasPressed()) {
                    if (inGame && client.currentScreen == null) module.toggle();
                }
            });
            if (!inGame) {
                for (Mod module : ModuleManager.INSTANCE.getEnabledModules()) {
                    if (module.getCategory() == Mod.Category.MOVEMENT) module.setEnabled(false);
                }
            } else if (!client.isPaused()) {
                for (Mod module : ModuleManager.INSTANCE.getEnabledModules()) module.onTick();
            }
        });
    }

    private KeyBinding register(String name, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.testclient." + name, InputUtil.Type.KEYSYM, key, "category.testclient"));
    }
}
