package github.amvern.twodimensionalreloaded.network;

import github.amvern.twodimensionalreloaded.TwoDimensionalReloaded;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record CycleBlockPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<CycleBlockPayload> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(TwoDimensionalReloaded.MOD_ID, "cycle_block"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CycleBlockPayload> CODEC =
        StreamCodec.of(
            (buf, payload) -> BlockPos.STREAM_CODEC.encode(buf, payload.pos()),
            buf -> new CycleBlockPayload(BlockPos.STREAM_CODEC.decode(buf))
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}