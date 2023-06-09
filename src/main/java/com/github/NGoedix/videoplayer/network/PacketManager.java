package com.github.NGoedix.videoplayer.network;

import com.github.NGoedix.videoplayer.Constants;
import com.github.NGoedix.videoplayer.block.entity.custom.TVBlockEntity;
import com.github.NGoedix.videoplayer.client.ClientHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class PacketManager {

    public static void receiveOpenVideoManager(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        Constants.LOGGER.info("Received open video message");

        BlockPos pos = buf.readBlockPos();
        String url = buf.readString();
        int tick = buf.readInt();
        int volume = buf.readInt();
        boolean loop = buf.readBoolean();

        ClientHandler.openVideoGUI(client, pos, url, tick, volume, loop);
    }

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Constants.LOGGER.info("Received update video message");

        server.execute(() -> {
            BlockPos pos = buf.readBlockPos();
            String url = buf.readString();
            int volume = buf.readInt();
            boolean loop = buf.readBoolean();
            boolean isPlaying = buf.readBoolean();
            boolean reset = buf.readBoolean();

            if (player.world.getBlockEntity(pos) instanceof TVBlockEntity tvBlockEntity) {
                tvBlockEntity.setBeingUsed(new UUID(0, 0));
                if (volume == -1) // NO UPDATE
                    return;

                tvBlockEntity.setUrl(url);
                Constants.LOGGER.info("Received url: " + url);
                tvBlockEntity.setVolume(volume);
                tvBlockEntity.setLoop(loop);
                tvBlockEntity.setPlaying(isPlaying);
                tvBlockEntity.notifyPlayer();

                if (reset)
                    tvBlockEntity.setTick(0);
            }
        });
    }
}
