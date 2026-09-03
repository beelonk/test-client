package com.test.ui;

import com.test.module.Mod;
import com.test.module.ModuleManager;
import com.test.module.render.InfoHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import java.util.Locale;

import java.util.Comparator;
import java.util.List;

public class Hud {


    private static final Minecraft mc = Minecraft.getInstance();
    public static void render (GuiGraphicsExtractor context, DeltaTracker renderTickCounter) {
        if (mc.player == null || mc.level == null || mc.gui.hud.isHidden()
                || mc.getDebugOverlay().showDebugScreen()) return;
        renderArrayList(context);
        if (ModuleManager.INSTANCE.getEnabledModules().stream().anyMatch(m -> m instanceof InfoHud)) {
            renderInfo(context);
        }
    }

    private static void renderInfo(GuiGraphicsExtractor context) {
        int y = 10;
        drawInfoLine(context, "Test Client | " + mc.getFps() + " FPS", y, 0xFF55FFFF);
        y += 12;
        drawInfoLine(context, String.format(Locale.ROOT, "XYZ: %.1f / %.1f / %.1f",
                mc.player.getX(), mc.player.getY(), mc.player.getZ()), y, 0xFFFFFFFF);
        y += 12;
        drawInfoLine(context, "Facing: " + mc.player.getDirection().getName(), y, 0xFFFFFFFF);
        y += 12;
        for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack armor = mc.player.getItemBySlot(slot);
            if (armor.isEmpty() || !armor.isDamageableItem()) continue;
            int remaining = armor.getMaxDamage() - armor.getDamageValue();
            int percent = (int) (100L * remaining / armor.getMaxDamage());
            drawInfoLine(context, armor.getHoverName().getString() + ": " + percent + "%",
                    y, percent <= 20 ? 0xFFFF5555 : 0xFFFFFFFF);
            y += 12;
        }
    }

    private static void drawInfoLine(GuiGraphicsExtractor context, String text, int y, int color) {
        context.fill(3, y - 2, 9 + mc.font.width(text), y + 10, 0x88000000);
        context.text(mc.font, text, 6, y, color, true);
    }

    public static void renderArrayList(GuiGraphicsExtractor context){
        int index = 0;
        int sWidth = mc.getWindow().getGuiScaledWidth();

        List<Mod> enabled = ModuleManager.INSTANCE.getEnabledModules();

        enabled.sort(Comparator.comparingInt((Mod m) -> mc.font.width(m.getDisplayName())).reversed());

        for (Mod mod :
                enabled) {
            context.text(mc.font, mod.getDisplayName(),
                    (sWidth - 4) - mc.font.width(mod.getDisplayName()),
                    10 + (index * mc.font.lineHeight), -1, true);
            index++;
        }
    }
}
