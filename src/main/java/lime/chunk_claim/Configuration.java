package lime.chunk_claim;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;

public class Configuration {

    public static class Common {
        public final ForgeConfigSpec.BooleanValue disable_fake_block_interaction;
        public final ForgeConfigSpec.BooleanValue disable_block_interaction;
        public final ForgeConfigSpec.BooleanValue disable_entity_interaction;
        public final ForgeConfigSpec.BooleanValue disable_entity_spawning;
        public final ForgeConfigSpec.BooleanValue disable_block_breaking;
        public final ForgeConfigSpec.BooleanValue disable_block_placing;
        public final ForgeConfigSpec.BooleanValue disable_attacking_entities;
        public final ForgeConfigSpec.BooleanValue disable_item_activation;
        public final ForgeConfigSpec.BooleanValue disable_explosions;
        public final ForgeConfigSpec.ConfigValue<Integer> max_claimed_chunks;

        Common(ForgeConfigSpec.Builder builder) {
            disable_fake_block_interaction = builder
                    .comment("Disallow non-player blocks/devices to place and/or interact with any blocks, doors, levers, buttons, etc. inside claimed chunk.")
                    .translation("chunkClaim.config.disable_fake_block_interaction")
                    .define("disable_fake_block_interaction", true);
            disable_block_interaction = builder
                    .comment("Disallow non-owner and non-guests to interact with any blocks, doors, levers, buttons, etc. inside claimed chunk.")
                    .translation("chunkClaim.config.disable_block_interaction")
                    .define("disable_block_interaction", true);
            disable_entity_interaction = builder
                    .comment("Disallow non-owner and non-guests to interact with any entities, such as cows, armour stands, etc. inside claimed chunk.")
                    .translation("chunkClaim.config.disable_entity_interaction")
                    .define("disable_entity_interaction", true);
            disable_entity_spawning = builder
                    .comment("Disable mob spawns inside claimed chunk.")
                    .translation("chunkClaim.config.disable_entity_spawning")
                    .define("disable_entity_spawning", false);
            disable_block_breaking = builder
                    .comment("Disallow non-owner and non-guests to break any blocks inside claimed chunk.")
                    .translation("chunkClaim.config.disable_block_breaking")
                    .define("disable_block_breaking", true);
            disable_block_placing = builder
                    .comment("Disallow non-owner and non-guests to place any blocks inside claimed chunk.")
                    .translation("chunkClaim.config.disable_block_placing")
                    .define("disable_block_placing", true);
            disable_attacking_entities = builder
                    .comment("Disallow non-owner and non-guests to attack any living thing that is inside claimed chunk.")
                    .translation("chunkClaim.config.disable_attacking_entities")
                    .define("disable_attacking_entities", false);
            disable_item_activation = builder
                    .comment("Disallow non-owner and non-guests to use (right-click) any items they hold inside claimed chunk.")
                    .translation("chunkClaim.config.disable_item_activation")
                    .define("disable_item_activation", true);
            disable_explosions = builder
                    .comment("Disable all and any explosion damage to blocks inside claimed chunk.")
                    .translation("chunkClaim.config.disable_explosions")
                    .define("disable_explosions", false);
            max_claimed_chunks = builder
                    .comment("Set the maximum claimable chunks per player.")
                    .translation("chunkClaim.config.max_claimed_chunks")
                    .define("max_claimed_chunks", 9);
        }
    }

    public static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }
}
