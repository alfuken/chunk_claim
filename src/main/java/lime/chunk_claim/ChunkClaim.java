package lime.chunk_claim;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ChunkClaim.MODID,
        name = ChunkClaim.NAME,
        version = ChunkClaim.VERSION,
        serverSideOnly = true,
        acceptableRemoteVersions = "*"
)
public class ChunkClaim {
    static final String MODID = "chunk_claim";
    static final String NAME = "Chunk Claim";
    static final String VERSION = "5";

    @Mod.Instance(MODID)
    public static ChunkClaim instance = new ChunkClaim();

    static Configuration config;
    static int max_chunks = 9;
    static boolean disable_fake_block_interaction = false;
    static boolean disable_block_interaction = true;
    static boolean disable_entity_interaction = true;
    static boolean disable_entity_spawning = false;
    static boolean disable_block_breaking = true;
    static boolean disable_block_placing = true;
    static boolean disable_attacking_entities = true;
    static boolean disable_item_activation = true;
    static boolean disable_explosions = true;
    static Logger logger;

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new ChunkClaimCommand());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        config = new Configuration(event.getSuggestedConfigurationFile());
        try {
            config.load();
            max_chunks = config.getInt("max claimed chunks", Configuration.CATEGORY_GENERAL,9,1,255,"Limits the number of chunks each player can claim");
            disable_fake_block_interaction = config.getBoolean("disable fake block interaction", Configuration.CATEGORY_GENERAL, disable_fake_block_interaction, "Disallow non-player blocks/devices to place and/or interact with any blocks, doors, levers, buttons, etc. inside claimed chunk.");
            disable_block_interaction = config.getBoolean("disable block interaction", Configuration.CATEGORY_GENERAL, disable_block_interaction, "Disallow non-owner and non-guests to interact with any blocks, doors, levers, buttons, etc. inside claimed chunk.");
            disable_entity_interaction = config.getBoolean("disable entity interaction", Configuration.CATEGORY_GENERAL, disable_entity_interaction, "Disallow non-owner and non-guests to interact with any entities, such as cows, armour stands, etc. inside claimed chunk.");
            disable_entity_spawning = config.getBoolean("disable entity spawning", Configuration.CATEGORY_GENERAL, disable_entity_spawning, "Disable mob spawns inside claimed chunk.");
            disable_block_breaking = config.getBoolean("disable block breaking", Configuration.CATEGORY_GENERAL, disable_block_breaking, "Disallow non-owner and non-guests to break any blocks inside claimed chunk.");
            disable_block_placing = config.getBoolean("disable block placing", Configuration.CATEGORY_GENERAL, disable_block_placing, "Disallow non-owner and non-guests to place any blocks inside claimed chunk.");
            disable_attacking_entities = config.getBoolean("disable attacking entities", Configuration.CATEGORY_GENERAL, disable_attacking_entities, "Disallow non-owner and non-guests to attack any living thing that is inside claimed chunk.");
            disable_item_activation = config.getBoolean("disable item activation", Configuration.CATEGORY_GENERAL, disable_item_activation, "Disallow non-owner and non-guests to use (right-click) any items they hold inside claimed chunk.");
            disable_explosions = config.getBoolean("disable explosions", Configuration.CATEGORY_GENERAL, disable_explosions, "Disable all and any explosion damage to blocks inside claimed chunk.");
        } catch (Exception e1) {
            ChunkClaim.logger.log(Level.ERROR, "Problem loading "+ChunkClaim.MODID+" config file!", e1);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new EventHandlers());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (config.hasChanged()) {
            config.save();
        }
        ClaimData.load();
    }
}
