package com.test.ui;

import com.test.module.Mod;
import com.test.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.List;

public class Hud {


    private static MinecraftClient mc = MinecraftClient.getInstance();
    public static void render (DrawContext context, RenderTickCounter renderTickCounter) {
        renderArrayList(context);
    }

    public static void renderArrayList(DrawContext context){
        int index = 0;
        int sWidth = mc.getWindow().getScaledWidth();
        int sHeight = mc.getWindow().getScaledHeight();

        List<Mod> enabled = ModuleManager.INSTANCE.getEnabledModules();

        enabled.sort(Comparator.comparingInt(m -> (int)mc.textRenderer.getWidth(((Mod)m).getDisplayName())));

        for (Mod mod :
                enabled) {
            context.drawText(mc.textRenderer, mod.getDisplayName(),
                    (sWidth - 4) - mc.textRenderer.getWidth(mod.getDisplayName()),
                    10 + (index * mc.textRenderer.fontHeight), -1, true);
            index++;
        }
    }
}
