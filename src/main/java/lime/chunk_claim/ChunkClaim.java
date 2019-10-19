package lime.chunk_claim;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lime.chunk_claim.commands.*;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Optional;

@Mod(ChunkClaim.MOD_ID)
public class ChunkClaim {
    private static final Logger LOGGER = LogManager.getLogger();

    static final String MOD_ID = "chunk_claim";

    public ChunkClaim() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.commonSpec);
        Configuration.loadConfig(Configuration.commonSpec, FMLPaths.CONFIGDIR.get().resolve("chunk_claim-common.toml"));
    }

    @Nonnull
    public static ResourceLocation getId(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    @Nonnull
    public static String getVersion() {
        Optional<? extends ModContainer> o = ModList.get().getModContainerById(MOD_ID);
        if (o.isPresent()) {
            return o.get().getModInfo().getVersion().toString();
        }
        return "NONE";
    }

    private void setup(final FMLCommonSetupEvent event) {
        ArgumentTypes.register("chunk_claim", Argument.class, new ArgumentSerializer<>(Argument::argument));

        ClaimData.load();
        LOGGER.info("Claim Data loaded!");
    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent event) {
        registerCommands(event.getCommandDispatcher());
    }

    private void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSource>literal("chunk_claim")
                        .then(CommandAddMember.register())
                        .then(CommandClaim.register())
//                        .then(CommandEvict.register())
                        .then(CommandInfo.register())
                        .then(CommandList.register())
                        .then(CommandRemoveMember.register())
                        .then(CommandSudoUnClaim.register())
                        .then(CommandUnClaim.register())
        );
    }
}
