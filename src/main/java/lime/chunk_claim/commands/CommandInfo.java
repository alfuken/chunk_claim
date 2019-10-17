package lime.chunk_claim.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import lime.chunk_claim.UtilsCommands;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class CommandInfo {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("info")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(ctx -> CommandInfo.executeCommand(ctx.getSource()));
    }

    private static int executeCommand(CommandSource command) {
        ServerPlayerEntity player = UtilsCommands.getServerPlayerEntity(command);

        if(player == null) {
            return 0;
        }

        command.sendFeedback(new StringTextComponent(ClaimManager.info(player)), true);

        return 1;
    }
}
