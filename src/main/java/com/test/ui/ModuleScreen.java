package com.test.ui;

import com.test.module.Mod;
import com.test.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ModuleScreen extends Screen {
    public ModuleScreen() {
        super(Text.literal("Test Client"));
    }

    @Override
    protected void init() {
        int buttonWidth = Math.min(280, width - 20);
        int y = 48;
        for (Mod module : ModuleManager.INSTANCE.getModules()) {
            addDrawableChild(ButtonWidget.builder(label(module), button -> {
                module.toggle();
                button.setMessage(label(module));
            }).dimensions((width - buttonWidth) / 2, y, buttonWidth, 20)
                    .tooltip(Tooltip.of(Text.literal(module.getDescription()))).build());
            y += 24;
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("Disable all"), button -> {
            for (Mod module : ModuleManager.INSTANCE.getModules()) module.setEnabled(false);
            clearAndInit();
        }).dimensions((width - buttonWidth) / 2, y + 4, buttonWidth, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> close())
                .dimensions((width - buttonWidth) / 2, y + 28, buttonWidth, 20).build());
    }

    private Text label(Mod module) {
        return Text.literal(module.getDisplayName() + ": " + (module.isEnabled() ? "ON" : "OFF"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 15, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, "Rebind keys in Options > Controls > Key Binds", width / 2, 30, 0xFFAAAAAA);
    }
}
