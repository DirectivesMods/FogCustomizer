package fogcustomizer;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import fogcustomizer.command.FogCustomizerCommand;
import fogcustomizer.config.FogConfig;
import fogcustomizer.event.FogEventHandler;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION,
    dependencies = "required-after:Forge@[11.15.0.1716,);",
    clientSideOnly=true, acceptedMinecraftVersions = "1.8.9")
public class FogCustomizer
{
    @Instance(Reference.MOD_ID)
    public static FogCustomizer instance;

    public static Logger logger;
    public static FogConfig config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        config = new FogConfig();
        CommandManager.INSTANCE.registerCommand(new FogCustomizerCommand());
        MinecraftForge.EVENT_BUS.register(new FogEventHandler());
    }
}
