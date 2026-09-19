package github.amvern.twodimensionalreloaded.client;

import github.amvern.twodimensionalreloaded.TwoDimensionalReloaded;
import github.amvern.twodimensionalreloaded.client.config.ClientConfig;
import github.amvern.twodimensionalreloaded.network.InteractionLayerPayload;
import github.amvern.twodimensionalreloaded.util.BlockPlacementGuide;
import github.amvern.twodimensionalreloaded.utils.LayerMode;
import github.amvern.twodimensionalreloaded.utils.Plane;

import static github.amvern.twodimensionalreloaded.utils.Plane.PLANE_ENTITY_FLAG;
import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;

public class TwoDimensionalReloadedClient implements ClientModInitializer {
    private LayerMode lastMode = LayerMode.BASE;
    private boolean lastRenderCulledBlocks = false;

    public static final KeyMapping.Category UTILITY_CATEGORY =
        new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(TwoDimensionalReloaded.MOD_ID, "utility")
        );

    public static KeyMapping faceAway = KeyMappingHelper.registerKeyMapping(new KeyMapping(
    "key.twodimensionalreloaded.face_away",
        InputConstants.KEY_B,
        UTILITY_CATEGORY
    ));

    public static KeyMapping faceForward = KeyMappingHelper.registerKeyMapping(new KeyMapping(
    "key.twodimensionalreloaded.face_forward",
        InputConstants.KEY_V,
        UTILITY_CATEGORY
    ));

    public static KeyMapping enablePlacementGuide = KeyMappingHelper.registerKeyMapping(new KeyMapping(
    "key.twodimensionalreloaded.enable_placement_guide",
        InputConstants.KEY_Y,
        UTILITY_CATEGORY
    ));

    public static KeyMapping screenPeek = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.twodimensionalreloaded.screen_peek",
            InputConstants.KEY_Z,
            UTILITY_CATEGORY
    ));

    public static ClientConfig CONFIG;

    public static LayerMode getActiveMode() {
        return faceAway.isDown() ? LayerMode.FACE_AWAY : faceForward.isDown() ? LayerMode.FACE_FORWARD : LayerMode.BASE;
    }

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ClientConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client)-> {
            Minecraft.getInstance().player.setAttached(PLANE_ENTITY_FLAG, true);
            client.levelRenderer.resetLevelRenderData();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null || client.getConnection() == null) return;

            LayerMode mode = getActiveMode();

            client.player.setGlowingTag(faceForward.isDown());

            boolean renderCulledBlocks = faceForward.isDown();
            if (renderCulledBlocks != lastRenderCulledBlocks) {
                lastRenderCulledBlocks = renderCulledBlocks;
                Plane.setRenderingCulledBlocks(renderCulledBlocks);
                reloadPlaneSections(client);
            }

            if(enablePlacementGuide.consumeClick()) {
                TwoDimensionalReloadedClient.CONFIG.renderBlockPlacementGuide = !TwoDimensionalReloadedClient.CONFIG.renderBlockPlacementGuide;
            }

            if (mode != lastMode) {
                lastMode = mode;
                ClientPlayNetworking.send(new InteractionLayerPayload(mode));
            }
        });

        LevelRenderEvents.END_MAIN.register(context -> {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player != null && TwoDimensionalReloadedClient.CONFIG.renderBlockPlacementGuide && player.getMainHandItem().getItem() instanceof BlockItem blockItem) {
                BlockPlacementGuide.renderPlacementGuide(context.poseStack(), context.submitNodeCollector());
            }
        });
    }

    private void reloadPlaneSections(Minecraft client) {
        if (client.level == null) return;

        client.levelRenderer.resetLevelRenderData();
    }
}