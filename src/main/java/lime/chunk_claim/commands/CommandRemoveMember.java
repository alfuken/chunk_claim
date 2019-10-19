package lime.chunk_claim.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import lime.chunk_claim.Argument;
import lime.chunk_claim.UtilsCommands;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class CommandRemoveMember {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("remove_member")
                .requires(cs -> cs.hasPermissionLevel(0))
                .then(Commands.argument("member", Argument.argument())
                        .executes(ctx -> CommandRemoveMember.executeCommand(ctx.getSource(), ctx.getArgument("member", String.class)))
                );
    }

    private static int executeCommand(CommandSource command, String name) {
        ServerPlayerEntity player = UtilsCommands.getServerPlayerEntity(command);

        if(player == null) {
            return 0;
        }

        command.sendFeedback(new StringTextComponent(ClaimManager.removeMember(player, name)), true);

        return 1;
    }
}
