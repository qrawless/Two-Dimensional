package github.amvern.twodimensionalreloaded.client.config;

import github.amvern.twodimensionalreloaded.TwoDimensionalReloaded;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.util.ARGB;

@Config(name = TwoDimensionalReloaded.MOD_ID)
public class ClientConfig implements ConfigData {
    public CameraMode cameraMode = CameraMode.DYNAMIC;
    public boolean renderFogEnvironments = false;

    public boolean renderBlockPlacementGuide = false;
    public boolean shouldRenderPlacementOutline = true;
    public RenderStyle blockRenderMode = RenderStyle.GHOST_BLOCK;
    public PlacementPreviewColors placeableColorEnum = PlacementPreviewColors.GREEN;
    public PlacementPreviewColors nonPlaceableColorEnum = PlacementPreviewColors.RED;
    public float blockAlphaValue = 0.5f;
    public float outlineAlphaValue = 1.0f;
    public float placementOutlineWidth = 4f;

    public enum CameraMode {
        STABLE,
        DYNAMIC
    }

    public enum RenderStyle {
        FULL_BLOCK,
        GHOST_BLOCK,
        OUTLINE_ONLY
    }

    public enum PlacementPreviewColors {
        GREEN(1.0f, 0f, 1f, 0f),
        RED(1.0f, 1f, 0f, 0f),
        BLUE(1.0f, 0f, 0f, 1f),
        YELLOW(1.0f, 1f, 1f, 0f),
        ORANGE(1.0f, 1f, 0.5f, 0f),
        PURPLE(1.0f, 0.5f, 0f, 0.5f),
        CYAN(1.0f, 0f, 1f, 1f),
        MAGENTA(1.0f, 1f, 0f, 1f),
        WHITE(1.0f, 1f, 1f, 1f),
        BLACK(1.0f, 0f, 0f, 0f);

        private final int color;

        PlacementPreviewColors(float alpha, float r, float g, float b) {
            this.color = ARGB.colorFromFloat(alpha, r, g, b);
        }

        public int getColor() {
            return color;
        }

        public int withAlpha(float alpha) {
            return ARGB.colorFromFloat(alpha, ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color));
        }
    }

    public int getPlaceableOutlineColor() {
        return placeableColorEnum.withAlpha(outlineAlphaValue);
    }

    public int getNonPlaceableOutlineColor() {
        return nonPlaceableColorEnum.withAlpha(outlineAlphaValue);
    }

    public int getPlaceableBlockColor() {
        return placeableColorEnum.withAlpha(blockAlphaValue);
    }

    public int getNonPlaceableBlockColor() {
        return nonPlaceableColorEnum.withAlpha(blockAlphaValue);
    }
}