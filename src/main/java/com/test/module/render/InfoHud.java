package com.test.module.render;

import com.test.module.Mod;
import com.mojang.blaze3d.platform.InputConstants;

public class InfoHud extends Mod {
    public InfoHud() {
        super("InfoHud", "Coordinates, direction, FPS and equipped armor durability", Category.RENDER);
        setDisplayName("Info HUD");
        setKey(InputConstants.KEY_H);
    }
}
