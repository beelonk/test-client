package com.test;

import com.test.module.Mod;
import com.test.module.ModuleManager;
import net.fabricmc.api.ModInitializer;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger logger = LoggerFactory.getLogger("testclient");
	public static final Client INSTANCE = new Client();

	private MinecraftClient mc = MinecraftClient.getInstance();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		logger.info("Hello Fabric world!");
	}

	public void onKeyPress(int key, int action){
		if (action == GLFW.GLFW_PRESS) logger.info("Key " + key + " was pressed");
		if (action == GLFW.GLFW_PRESS) {
			for (Mod module : ModuleManager.INSTANCE.getModules()) {
				if (key == module.getKey()) module.toggle();
			}
		}
	}

	public void onTick(){
		if (mc.player != null) {
			for (Mod module :
					ModuleManager.INSTANCE.getEnabledModules()) {
				module.onTick();
			}
		}
	}
}