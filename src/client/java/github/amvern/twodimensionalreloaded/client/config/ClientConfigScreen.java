package github.amvern.twodimensionalreloaded.client.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClientConfigScreen {

    public static Screen create(Screen parent, ClientConfig config) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.literal("Two Dimensional: Reloaded Options"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory generalSettings = builder.getOrCreateCategory(Component.literal("General"));
        SubCategoryBuilder blockPlacementGuideOptions = entryBuilder.startSubCategory(Component.literal("Block Placement Guide Options"));

        generalSettings.addEntry(entryBuilder.startTextDescription(Component.literal("Toggle whether camera follows mouse movement")).build());
        generalSettings.addEntry(entryBuilder.startEnumSelector(Component.literal("Camera Mode"),
            ClientConfig.CameraMode.class, config.cameraMode)
            .setDefaultValue(ClientConfig.CameraMode.DYNAMIC)
            .setSaveConsumer(value ->config.cameraMode = value)
            .setEnumNameProvider(style -> Component.nullToEmpty(style.name().replace("_", " ")))
            .build()
        );

        blockPlacementGuideOptions.add(entryBuilder.startBooleanToggle(Component.literal("Render Block Placement Guide"), config.renderBlockPlacementGuide)
            .setDefaultValue(false)
            .setSaveConsumer(value -> config.renderBlockPlacementGuide = value)
            .setTooltip(Component.literal("Render placement preview"))
            .build()
        );

        blockPlacementGuideOptions.add(entryBuilder.startEnumSelector(
            Component.literal("Placement Preview Style"),
                ClientConfig.RenderStyle.class,
                config.blockRenderMode
            ).setDefaultValue(ClientConfig.RenderStyle.GHOST_BLOCK)
            .setSaveConsumer(value -> config.blockRenderMode = value)
            .setEnumNameProvider(style -> Component.nullToEmpty(style.name().replace("_", " ")))
            .build()
        );

        blockPlacementGuideOptions.add(entryBuilder.startIntSlider(
            Component.literal("Ghost Mode Transparency Level"),
            (int)(config.blockAlphaValue * 10),
            0,
            10)
            .setSaveConsumer(value -> config.blockAlphaValue = value / 10f)
            .setDefaultValue((int)(0.5f * 10))
            .setTextGetter(value -> Component.literal(String.format("%.1f", value / 10f)))
            .build()
        );

        blockPlacementGuideOptions.add(entryBuilder.startBooleanToggle(
            Component.literal("Show Outline"),
                config.shouldRenderPlacementOutline
            ).setDefaultValue(true)
            .setSaveConsumer(value -> config.shouldRenderPlacementOutline = value)
            .setTooltip(Component.literal("Render outline around placement preview"))
            .build()
        );

        blockPlacementGuideOptions.add(entryBuilder.startEnumSelector(
            Component.literal("Placeable Color"),
                ClientConfig.PlacementPreviewColors.class,
                config.placeableColorEnum
            ).setSaveConsumer(value -> config.placeableColorEnum = value)
            .setDefaultValue(ClientConfig.PlacementPreviewColors.GREEN)
            .setEnumNameProvider(c -> Component.literal(c.name()))
            .build()
        );

        blockPlacementGuideOptions.add(entryBuilder.startEnumSelector(
            Component.literal("Non-Placeable Color"),
                ClientConfig.PlacementPreviewColors.class,
                config.nonPlaceableColorEnum
            ).setSaveConsumer(value -> config.nonPlaceableColorEnum = value)
            .setDefaultValue(ClientConfig.PlacementPreviewColors.RED)
            .setEnumNameProvider(c -> Component.literal(c.name()))
            .build()
        );

        blockPlacementGuideOptions.add(entryBuilder.startIntSlider(
            Component.literal("Outline Width"),
            (int)(config.placementOutlineWidth * 10),
            (1 * 10),
            (8 * 10))
            .setSaveConsumer(value -> config.placementOutlineWidth = value / 10f)
            .setDefaultValue((int)(4f * 10))
            .setTextGetter(value -> Component.literal(String.format("%.1f", value / 10f)))
            .build()
        );

        blockPlacementGuideOptions.add(entryBuilder.startIntSlider(
            Component.literal("Outline Transparency"),
            (int)(config.outlineAlphaValue * 10),
            0,
            10)
            .setSaveConsumer(value -> config.outlineAlphaValue = value / 10f)
            .setTextGetter(value -> Component.literal(String.format("%.1f", value / 10f)))
            .setDefaultValue((int)(0.5f * 10))
            .build()
        );

        generalSettings.addEntry(entryBuilder.startTextDescription(Component.literal("Toggle Fog Environments for Water/Lava/Powerdered Snow.")).build());
        generalSettings.addEntry(entryBuilder.startBooleanToggle(
            Component.literal("Render Fog"),
            config.renderFogEnvironments)
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.renderFogEnvironments = value)
            .build()
        );

        generalSettings.addEntry(blockPlacementGuideOptions.build());

        builder.setSavingRunnable(()-> {
            AutoConfig.getConfigHolder(ClientConfig.class).save();
        });

        return builder.build();
    }
}