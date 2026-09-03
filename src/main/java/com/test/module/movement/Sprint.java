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
        if (mc.player == null || mc.gui.screen() != null) return;
        if (mc.player.input.keyPresses.forward() && !mc.player.isShiftKeyDown()
                && !mc.player.horizontalCollision && !mc.player.isUsingItem()
                && (mc.player.getFoodData().getFoodLevel() > 6
                    || mc.player.getAbilities().mayfly)) {
            mc.player.setSprinting(true);
        }
        super.onTick();
    }
}
