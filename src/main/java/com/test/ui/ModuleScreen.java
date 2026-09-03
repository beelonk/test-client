package com.test.ui;

import com.test.module.Mod;
import com.test.module.ModuleManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ModuleScreen extends Screen {
    public ModuleScreen() {
        super(Component.literal("Test Client"));
    }

    @Override
    protected void init() {
        int buttonWidth = Math.min(280, width - 20);
        int y = 48;
        for (Mod module : ModuleManager.INSTANCE.getModules()) {
            addRenderableWidget(Button.builder(label(module), button -> {
                module.toggle();
                button.setMessage(label(module));
            }).bounds((width - buttonWidth) / 2, y, buttonWidth, 20)
                    .tooltip(Tooltip.create(Component.literal(module.getDescription()))).build());
            y += 24;
        }
        addRenderableWidget(Button.builder(Component.literal("Disable all"), button -> {
            for (Mod module : ModuleManager.INSTANCE.getModules()) module.setEnabled(false);
            rebuildWidgets();
        }).bounds((width - buttonWidth) / 2, y + 4, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                .bounds((width - buttonWidth) / 2, y + 28, buttonWidth, 20).build());
    }

    private Component label(Mod module) {
        return Component.literal(module.getDisplayName() + ": " + (module.isEnabled() ? "ON" : "OFF"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(font, title, width / 2, 15, 0xFFFFFFFF);
        context.centeredText(font, "Rebind keys in Options > Controls > Key Binds", width / 2, 30, 0xFFAAAAAA);
    }
}
