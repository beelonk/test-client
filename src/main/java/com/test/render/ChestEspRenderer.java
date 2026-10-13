package com.test.render;

import com.test.module.ModuleManager;
import com.test.module.render.ChestEsp;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Uses Blaze3D rather than OpenGL, including on the Vulkan backend. */
public final class ChestEspRenderer {
    private static final RenderPipeline PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(snippetFrom(RenderPipelines.DEBUG_FILLED_BOX))
                    .withLocation(Identifier.fromNamespaceAndPath("testclient", "chest_esp"))
                    .withDepthStencilState(Optional.empty())
                    .build());
    private static final RenderType RENDER_TYPE = RenderType.create(
            "testclient_chest_esp", RenderSetup.builder(PIPELINE).sortOnUpload().createRenderSetup());

    private static RenderPipeline.Snippet snippetFrom(RenderPipeline source) {
        return new RenderPipeline.Snippet(
                source.getShaders(), Optional.of(source.getShaderDefines()),
                Optional.of(source.getBindGroupLayouts()),
                source.getColorTargetStates().toArray(com.mojang.renderpearl.api.pipeline.ColorTargetState[]::new),
                source.getColorTargetStates().size(), Optional.of(source.getDepthStencilState()),
                Optional.of(source.getPolygonMode()), Optional.of(source.isCull()),
                source.getVertexFormatBindings().toArray(com.mojang.renderpearl.api.vertex.VertexFormat[]::new),
                Optional.of(source.getPrimitiveTopology()), source.pushConstantSize());
    }
    private static final int[][] FACES = {
            {0, 1, 3, 2}, {5, 4, 6, 7}, {4, 0, 2, 6},
            {1, 5, 7, 3}, {2, 3, 7, 6}, {4, 5, 1, 0}
    };
    private static List<Box> frame = List.of();

    private ChestEspRenderer() { }

    public static void register() {
        LevelExtractionEvents.END_EXTRACTION.register(ChestEspRenderer::extract);
        LevelRenderEvents.COLLECT_SUBMITS.register(ChestEspRenderer::draw);
    }

    private static void extract(LevelExtractionContext context) {
        // Replace every frame, including when disabled or switching worlds.
        frame = List.of();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.gui.hud.isHidden()) return;
        ChestEsp module = ModuleManager.INSTANCE.getModules().stream()
                .filter(ChestEsp.class::isInstance).map(ChestEsp.class::cast).findFirst().orElseThrow();
        List<Box> boxes = new ArrayList<>();
        for (var pos : module.positions(context.level())) {
            if (!context.level().hasChunkAt(pos)
                    || pos.distToCenterSqr(mc.player.position()) > ChestEsp.RANGE * ChestEsp.RANGE) continue;
            BlockState state = context.level().getBlockState(pos);
            int color;
            if (state.is(Blocks.TRAPPED_CHEST)) color = 0x66FF4444;
            else if (state.is(Blocks.ENDER_CHEST)) color = 0x669955FF;
            else if (state.is(Blocks.CHEST)) color = 0x66FFCC33;
            else continue;
            // The shape accounts for double-chest halves and their facing.
            var bounds = state.getShape(context.level(), pos).bounds().move(pos);
            boxes.add(new Box(bounds.minX, bounds.minY, bounds.minZ,
                    bounds.maxX, bounds.maxY, bounds.maxZ, color));
        }
        frame = List.copyOf(boxes);
    }

    private static void draw(LevelRenderContext context) {
        List<Box> boxes = frame;
        if (boxes.isEmpty()) return;
        var camera = context.levelState().cameraRenderState.pos;
        context.submitNodeCollector().submitCustomGeometry(context.poseStack(), RENDER_TYPE, (pose, vertices) -> {
            for (Box box : boxes) {
                // Subtract in double precision before casting: stable far from spawn.
                float[] xs = {(float) (box.minX - camera.x), (float) (box.maxX - camera.x)};
                float[] ys = {(float) (box.minY - camera.y), (float) (box.maxY - camera.y)};
                float[] zs = {(float) (box.minZ - camera.z), (float) (box.maxZ - camera.z)};
                for (int[] face : FACES) {
                    for (int corner : face) {
                        vertices.addVertex(pose, xs[corner & 1], ys[(corner >> 1) & 1], zs[(corner >> 2) & 1])
                                .setColor(box.color);
                    }
                }
            }
        });
    }

    public static void close() {
        frame = List.of();
    }

    private record Box(double minX, double minY, double minZ,
                       double maxX, double maxY, double maxZ, int color) { }
}
