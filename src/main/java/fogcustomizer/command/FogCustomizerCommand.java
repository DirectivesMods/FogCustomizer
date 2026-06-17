package fogcustomizer.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import fogcustomizer.FogCustomizer;
import fogcustomizer.Reference;

@Command(value = Reference.MOD_ID, aliases = { "fog" }, description = "Open the FogCustomizer OneConfig menu.")
public class FogCustomizerCommand
{
    @Main
    private void handle()
    {
        if (FogCustomizer.config != null)
        {
            FogCustomizer.config.openGui();
        }
    }
}
