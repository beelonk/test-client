package com.test.module.movement;

import com.test.module.Mod;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.network.ClientPlayerEntity;

public class Flight extends Mod {
    private ClientPlayerEntity affectedPlayer;
    private boolean originalAllowFlying;
    private boolean originalFlying;

    public Flight()
    {
        super("Flight", "allows you to fly", Category.MOVEMENT);
        this.setKey(GLFW.GLFW_KEY_G);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (affectedPlayer != mc.player) {
            restore();
            affectedPlayer = mc.player;
            originalAllowFlying = mc.player.getAbilities().allowFlying
                    && !mc.player.isCreative() && !mc.player.isSpectator();
            originalFlying = mc.player.getAbilities().flying;
        }
        mc.player.getAbilities().allowFlying = true;
        super.onTick();
    }

    @Override
    public void onDisable() {
        restore();
        super.onDisable();
    }

    private void restore() {
        if (affectedPlayer != null) {
            // Creative/spectator abilities belong to the game, not this module.
            if (!affectedPlayer.isCreative() && !affectedPlayer.isSpectator()) {
                affectedPlayer.getAbilities().allowFlying = originalAllowFlying;
                affectedPlayer.getAbilities().flying = originalAllowFlying && originalFlying;
            }
            affectedPlayer = null;
        }
    }
}
