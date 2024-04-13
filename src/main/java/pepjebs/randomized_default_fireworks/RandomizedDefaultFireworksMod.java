package pepjebs.randomized_default_fireworks;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import pepjebs.randomized_default_fireworks.config.RandomizedDefaultFireworksConfig;

public class RandomizedDefaultFireworksMod implements ModInitializer {

    public static RandomizedDefaultFireworksConfig CONFIG = null;
    public static String DEFAULT_BLOCKLIST_COLORS = "black,gray,light_gray,white";

    @Override
    public void onInitialize() {
        if(FabricLoader.getInstance().isModLoaded("cloth-config")) {
            AutoConfig.register(RandomizedDefaultFireworksConfig.class, JanksonConfigSerializer::new);
            CONFIG = AutoConfig.getConfigHolder(RandomizedDefaultFireworksConfig.class).getConfig();
        }
    }
}
