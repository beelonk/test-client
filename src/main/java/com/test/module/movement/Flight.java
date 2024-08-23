package com.test.module.movement;

import com.test.module.Mod;
import org.lwjgl.glfw.GLFW;

public class Flight extends Mod {

    public Flight()
    {
        super("Flight", "allows you to fly", Category.MOVEMENT);
        this.setKey(GLFW.GLFW_KEY_G);
    }

    @Override
    public void onTick() {
        mc.player.getAbilities().allowFlying = true;
        super.onTick();
    }

    @Override
    public void onDisable() {
        mc.player.getAbilities().allowFlying = false;
        super.onDisable();
    }
}
