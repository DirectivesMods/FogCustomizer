package fogcustomizer.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.annotations.Dropdown;
import cc.polyfrost.oneconfig.config.annotations.Slider;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;
import fogcustomizer.FogCustomizer;
import fogcustomizer.Reference;

public class FogConfig extends Config
{
    @Switch(
        name = "Override Fog Color",
        description = "Use the selected color for the fog.",
        size = OptionSize.SINGLE,
        category = "Fog",
        subcategory = "Color"
    )
    public static boolean overrideFogColor = true;

    @Color(
        name = "Fog Color",
        description = "The color applied to the fog.",
        allowAlpha = false,
        size = OptionSize.SINGLE,
        category = "Fog",
        subcategory = "Color"
    )
    public static OneColor fogColor = new OneColor(170, 205, 255);

    @Slider(
        name = "Color Opacity",
        description = "How visible the selected fog color is.",
        min = 0f,
        max = 100f,
        category = "Fog",
        subcategory = "Color"
    )
    public static float colorStrength = 100f;

    @Switch(
        name = "Override Fog Intensity",
        description = "Scale the distance and density of the fog.",
        size = OptionSize.SINGLE,
        category = "Fog",
        subcategory = "Intensity"
    )
    public static boolean overrideFogIntensity = true;

    @Dropdown(
        name = "Falloff",
        description = "Smooth modes start fog at the camera and avoid the visible linear fog ring. Constant is a flat, even haze with no distance falloff.",
        options = { "Smooth", "Soft", "Linear", "Constant" },
        category = "Fog",
        subcategory = "Intensity"
    )
    public static int fogFalloff = 0;

    @Slider(
        name = "Intensity Multiplier",
        description = "1.0 is vanilla fog. Higher values pull fog closer to the player; 0 disables visible fog.",
        min = 0f,
        max = 20f,
        category = "Fog",
        subcategory = "Intensity"
    )
    public static float intensityMultiplier = 1f;

    public FogConfig()
    {
        super(new Mod(Reference.MOD_NAME, ModType.UTIL_QOL), Reference.MOD_ID + ".json");
        initialize();

        addDependency("fogColor", "Override Fog Color", () -> overrideFogColor);
        addDependency("colorStrength", "Override Fog Color", () -> overrideFogColor);
        addDependency("fogFalloff", "Override Fog Color or Override Fog Intensity", () -> overrideFogIntensity || overrideFogColor);
        addDependency("intensityMultiplier", "Override Fog Intensity", () -> overrideFogIntensity);
    }

    public static boolean isActive()
    {
        return FogCustomizer.config != null && FogCustomizer.config.enabled;
    }
}
