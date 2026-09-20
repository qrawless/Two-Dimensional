package github.amvern.twodimensionalreloaded.utils;

import github.amvern.twodimensionalreloaded.access.InteractionLayerGetterSetter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.List;

public final class BlockStateCycler {
    private static final List<String> DIRECTION_PROPERTY_NAMES = List.of(
        "facing", "facing_horizontal", "axis", "rotation", "orientation"
    );

    private BlockStateCycler() {}

    public static void cycle(ServerPlayer player, BlockPos pos) {
        LayerMode mode = ((InteractionLayerGetterSetter) player).getInteractionLayer();
        if (!Plane.isLayerAllowed(pos, mode)) return;
        if (!Plane.isWithinReach(player, pos)) return;

        Level level = player.level();
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return;

        Property<?> property = directionProperty(state);
        if (property == null) return;

        boolean reverse = player.isSecondaryUseActive();

        List<?> values = property.getPossibleValues();
        int currentIndex = values.indexOf(state.getValue(property));
        if (currentIndex < 0) return;
        int direction = reverse ? -1 : 1;
        int nextIndex = Math.floorMod(currentIndex + direction, values.size());

        BlockState newState = setValue(state, property, values.get(nextIndex));
        level.setBlock(pos, newState, 18);
    }

    private static Property<?> directionProperty(BlockState state) {
        StateDefinition<Block, BlockState> definition = state.getBlock().getStateDefinition();
        Collection<Property<?>> properties = definition.getProperties();
        for (String name : DIRECTION_PROPERTY_NAMES) {
            for (Property<?> property : properties) {
                if (property.getName().equals(name)) {
                    return property;
                }
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState setValue(BlockState state, Property<?> property, Object value) {
        return state.setValue((Property) property, (Comparable) value);
    }
}