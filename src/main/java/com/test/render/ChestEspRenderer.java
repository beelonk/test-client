package com.test.render;

import com.test.module.ModuleManager;
import com.test.module.render.ChestEsp;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/** Uses Blaze3D rather than OpenGL, including on the Vulkan backend. */
public final class ChestEspRenderer {
    private static final RenderPipeline PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath("testclient", "chest_esp"))
                    .withDepthStencilState(Optional.empty())
                    .build());
    private static final int[][] FACES = {
            {0, 1, 3, 2}, {5, 4, 6, 7}, {4, 0, 2, 6},
            {1, 5, 7, 3}, {2, 3, 7, 6}, {4, 5, 1, 0}
    };
    private static StagedVertexBuffer buffer;
    private static List<Box> frame = List.of();

    private ChestEspRenderer() { }

    public static void register() {
        LevelExtractionEvents.END_EXTRACTION.register(ChestEspRenderer::extract);
        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(ChestEspRenderer::draw);
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
        if (buffer == null) buffer = new StagedVertexBuffer(() -> "Test Client Chest ESP", 65536);
        var draw = buffer.appendDraw(PIPELINE.getVertexFormatBinding(0), PIPELINE.getPrimitiveTopology(),
                RenderSystem.getProjectionType().vertexSorting());
        try {
            var vertices = buffer.getVertexBuilder(draw);
            var camera = context.levelState().cameraRenderState.pos;
            var matrix = context.poseStack().last().pose();
            for (Box box : boxes) {
                // Subtract in double precision before casting: stable far from spawn.
                float[] xs = {(float) (box.minX - camera.x), (float) (box.maxX - camera.x)};
                float[] ys = {(float) (box.minY - camera.y), (float) (box.maxY - camera.y)};
                float[] zs = {(float) (box.minZ - camera.z), (float) (box.maxZ - camera.z)};
                for (int[] face : FACES) {
                    for (int corner : face) {
                        vertices.addVertex(matrix, xs[corner & 1], ys[(corner >> 1) & 1], zs[(corner >> 2) & 1])
                                .setColor(box.color);
                    }
                }
            }
            buffer.upload();
            var info = buffer.getExecuteInfo(draw);
            if (info == null) return;
            var target = Minecraft.getInstance().gameRenderer.mainRenderTarget();
            var colorTexture = target.getColorTextureView();
            if (colorTexture == null) return;
            var transform = RenderSystem.getDynamicUniforms().writeTransform(
                    RenderSystem.getModelViewMatrixCopy(), new Vector4f(1, 1, 1, 1), new Vector3f(), new Matrix4f());
            try (var pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                    () -> "Test Client Chest ESP", colorTexture, Optional.empty(),
                    target.getDepthTextureView(), OptionalDouble.empty())) {
                pass.setPipeline(PIPELINE);
                RenderSystem.bindDefaultUniforms(pass);
                pass.setUniform("DynamicTransforms", transform);
                pass.setVertexBuffer(0, info.vertexBuffer().slice());
                pass.setIndexBuffer(info.indexBuffer(), info.indexType());
                pass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
            }
        } finally {
            buffer.endFrame();
        }
    }

    public static void close() {
        frame = List.of();
        if (buffer != null) {
            buffer.close();
            buffer = null;
        }
    }

    private record Box(double minX, double minY, double minZ,
                       double maxX, double maxY, double maxZ, int color) { }
}
