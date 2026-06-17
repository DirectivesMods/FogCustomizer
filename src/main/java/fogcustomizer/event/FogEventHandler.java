package fogcustomizer.event;

import org.lwjgl.opengl.GL11;

import cc.polyfrost.oneconfig.config.core.OneColor;
import fogcustomizer.config.FogConfig;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FogEventHandler
{
    @SubscribeEvent
    public void onFogColors(EntityViewRenderEvent.FogColors event)
    {
        if (FogConfig.isActive() == false || FogConfig.overrideFogColor == false || FogConfig.fogColor == null)
        {
            return;
        }

        OneColor color = FogConfig.fogColor;

        event.red = color.getRed() / 255f;
        event.green = color.getGreen() / 255f;
        event.blue = color.getBlue() / 255f;
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event)
    {
        if (FogConfig.isActive() == false || shouldScaleFog() == false || event.block == null)
        {
            return;
        }

        Material material = event.block.getMaterial();

        if (material != Material.water && material != Material.lava)
        {
            return;
        }

        float intensity = getEffectiveIntensity();
        float baseDensity = material == Material.lava ? 2f : 0.1f;

        GlStateManager.setFog(FogConfig.fogFalloff == 1 ? GL11.GL_EXP : GL11.GL_EXP2);
        event.density = baseDensity * intensity;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderFog(EntityViewRenderEvent.RenderFogEvent event)
    {
        if (FogConfig.isActive() == false || shouldScaleFog() == false)
        {
            return;
        }

        float farPlaneDistance = Math.max(event.farPlaneDistance, 1f);
        float intensity = getEffectiveIntensity();

        if (intensity <= 0f)
        {
            GlStateManager.setFog(GL11.GL_LINEAR);
            float hiddenFogDistance = farPlaneDistance * 4f;
            GlStateManager.setFogStart(hiddenFogDistance);
            GlStateManager.setFogEnd(hiddenFogDistance + 1f);
            return;
        }

        if (FogConfig.fogFalloff == 0)
        {
            GlStateManager.setFog(GL11.GL_EXP2);
            GlStateManager.setFogDensity(1.5f * intensity / farPlaneDistance);
            return;
        }

        if (FogConfig.fogFalloff == 1)
        {
            GlStateManager.setFog(GL11.GL_EXP);
            GlStateManager.setFogDensity(3f * intensity / farPlaneDistance);
            return;
        }

        if (FogConfig.fogFalloff == 3)
        {
            GlStateManager.setFog(GL11.GL_LINEAR);

            float opacity = clamp(intensity / 20f, 0f, 1f);
            float scale = farPlaneDistance * 1024f;
            float end = (1f - opacity) * scale;

            GlStateManager.setFogStart(end - scale);
            GlStateManager.setFogEnd(end);
            return;
        }

        GlStateManager.setFog(GL11.GL_LINEAR);

        float end = farPlaneDistance / intensity;
        float startFactor = intensity > 1f ? 0.75f / intensity : 0.75f;
        float start = event.fogMode < 0 ? 0f : end * startFactor;
        end = Math.max(start + 0.01f, end);

        GlStateManager.setFogStart(start);
        GlStateManager.setFogEnd(end);
    }

    private static boolean shouldScaleFog()
    {
        return FogConfig.overrideFogIntensity || getColorOpacity() < 1f;
    }

    private static float getEffectiveIntensity()
    {
        float intensity = FogConfig.overrideFogIntensity ? clamp(FogConfig.intensityMultiplier, 0f, 20f) : 1f;
        return intensity * getColorOpacity();
    }

    private static float getColorOpacity()
    {
        if (FogConfig.overrideFogColor == false)
        {
            return 1f;
        }

        return clamp(FogConfig.colorStrength / 100f, 0f, 1f);
    }

    private static float clamp(float value, float min, float max)
    {
        return Math.max(min, Math.min(max, value));
    }
}
