package pepjebs.randomized_default_fireworks.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import pepjebs.randomized_default_fireworks.RandomizedDefaultFireworksMod;

@Config(name = "randomized_default_fireworks")
public class RandomizedDefaultFireworksConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip()
    @Comment("If enabled, default Fireworks will explode even during Elytra flight.")
    public boolean generateDuringElytra = false;

    @ConfigEntry.Gui.Tooltip()
    @Comment("Comma-separated list of dye colors to avoid when creating Explosions.")
    public String blocklistColors = RandomizedDefaultFireworksMod.DEFAULT_BLOCKLIST_COLORS;
}
