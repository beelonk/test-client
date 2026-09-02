package com.test.module.movement;

import com.test.module.Mod;
import org.lwjgl.glfw.GLFW;

public class Sprint extends Mod {

    public Sprint(){
        super("Sprint", "Keeps your sprint", Category.MOVEMENT);
        this.setKey(GLFW.GLFW_KEY_V);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.currentScreen != null) return;
        if (mc.player.input.movementForward > 0 && !mc.player.isSneaking()
                && !mc.player.horizontalCollision && !mc.player.isUsingItem()
                && (mc.player.getHungerManager().getFoodLevel() > 6
                    || mc.player.getAbilities().allowFlying)) {
            mc.player.setSprinting(true);
        }
        super.onTick();
    }
}
