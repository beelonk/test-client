package com.test.ui;

import com.test.module.Mod;
import com.test.module.ModuleManager;
import com.test.module.render.InfoHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import java.util.Locale;

import java.util.Comparator;
import java.util.List;

public class Hud {


    private static MinecraftClient mc = MinecraftClient.getInstance();
    public static void render (DrawContext context, RenderTickCounter renderTickCounter) {
        if (mc.player == null || mc.world == null || mc.options.hudHidden
                || mc.getDebugHud().shouldShowDebugHud()) return;
        renderArrayList(context);
        if (ModuleManager.INSTANCE.getEnabledModules().stream().anyMatch(m -> m instanceof InfoHud)) {
            renderInfo(context);
        }
    }

    private static void renderInfo(DrawContext context) {
        int y = 10;
        drawInfoLine(context, "Test Client | " + mc.getCurrentFps() + " FPS", y, 0xFF55FFFF);
        y += 12;
        drawInfoLine(context, String.format(Locale.ROOT, "XYZ: %.1f / %.1f / %.1f",
                mc.player.getX(), mc.player.getY(), mc.player.getZ()), y, 0xFFFFFFFF);
        y += 12;
        drawInfoLine(context, "Facing: " + mc.player.getHorizontalFacing().asString(), y, 0xFFFFFFFF);
        y += 12;
        for (ItemStack armor : mc.player.getArmorItems()) {
            if (armor.isEmpty() || !armor.isDamageable()) continue;
            int remaining = armor.getMaxDamage() - armor.getDamage();
            int percent = (int) (100L * remaining / armor.getMaxDamage());
            drawInfoLine(context, armor.getName().getString() + ": " + percent + "%",
                    y, percent <= 20 ? 0xFFFF5555 : 0xFFFFFFFF);
            y += 12;
        }
    }

    private static void drawInfoLine(DrawContext context, String text, int y, int color) {
        context.fill(3, y - 2, 9 + mc.textRenderer.getWidth(text), y + 10, 0x88000000);
        context.drawText(mc.textRenderer, text, 6, y, color, true);
    }

    public static void renderArrayList(DrawContext context){
        int index = 0;
        int sWidth = mc.getWindow().getScaledWidth();

        List<Mod> enabled = ModuleManager.INSTANCE.getEnabledModules();

        enabled.sort(Comparator.comparingInt((Mod m) -> mc.textRenderer.getWidth(m.getDisplayName())).reversed());

        for (Mod mod :
                enabled) {
            context.drawText(mc.textRenderer, mod.getDisplayName(),
                    (sWidth - 4) - mc.textRenderer.getWidth(mod.getDisplayName()),
                    10 + (index * mc.textRenderer.fontHeight), -1, true);
            index++;
        }
    }
}
