package com.test.module.render;

import com.test.module.Mod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** A bounded cache of positions only: no chunk loads, packets or container access. */
public class ChestEsp extends Mod {
    public static final int RANGE = 64;
    public static final int MAX_CHESTS = 512;
    private ClientLevel cachedLevel;
    private List<BlockPos> positions = List.of();
    private int scanCountdown;

    public ChestEsp() {
        super("ChestEsp", "Show loaded chests through walls within 64 blocks (gold/red/purple)", Category.RENDER);
        setDisplayName("Chest ESP");
        setKey(GLFW.GLFW_KEY_C);
    }

    @Override
    public void onEnable() {
        scanCountdown = 0;
    }

    @Override
    public void onDisable() {
        onWorldLeave();
    }

    @Override
    public void onWorldLeave() {
        positions = List.of();
        cachedLevel = null;
        scanCountdown = 0;
    }

    @Override
    public void onTick() {
        if (mc.level == null || mc.player == null) {
            onDisable();
            return;
        }
        if (mc.level != cachedLevel) {
            positions = List.of();
            cachedLevel = mc.level;
            scanCountdown = 0;
        }
        if (scanCountdown-- > 0) return;
        scanCountdown = 9;
        List<BlockPos> found = new ArrayList<>();
        BlockPos origin = mc.player.blockPosition();
        int chunkRadius = RANGE / 16 + 1;
        for (int x = (origin.getX() >> 4) - chunkRadius; x <= (origin.getX() >> 4) + chunkRadius; x++) {
            for (int z = (origin.getZ() >> 4) - chunkRadius; z <= (origin.getZ() >> 4) + chunkRadius; z++) {
                LevelChunk chunk = mc.level.getChunkSource().getChunk(x, z, ChunkStatus.FULL, false);
                if (chunk == null) continue;
                chunk.getBlockEntities().forEach((pos, entity) -> {
                    if ((entity instanceof ChestBlockEntity || entity instanceof EnderChestBlockEntity)
                            && pos.distToCenterSqr(mc.player.position()) <= RANGE * RANGE) {
                        found.add(pos.immutable());
                    }
                });
            }
        }
        found.sort(Comparator.comparingDouble(pos -> pos.distToCenterSqr(mc.player.position())));
        positions = List.copyOf(found.subList(0, Math.min(MAX_CHESTS, found.size())));
    }

    public List<BlockPos> positions(ClientLevel level) {
        return isEnabled() && cachedLevel == level ? positions : List.of();
    }
}
