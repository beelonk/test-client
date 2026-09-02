package com.test.module.render;

import com.test.module.Mod;
import org.lwjgl.glfw.GLFW;

public class InfoHud extends Mod {
    public InfoHud() {
        super("InfoHud", "Coordinates, direction, FPS and equipped armor durability", Category.RENDER);
        setDisplayName("Info HUD");
        setKey(GLFW.GLFW_KEY_H);
    }
}
