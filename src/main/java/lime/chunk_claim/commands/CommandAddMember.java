package lime.chunk_claim.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import lime.chunk_claim.UtilsCommands;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class CommandAddMember {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("add_member")
                .requires(cs -> cs.hasPermissionLevel(0))
                .then(Commands.argument("member", EntityArgument.players())
                        .executes(ctx -> CommandAddMember.executeCommand(ctx.getSource(), EntityArgument.getPlayer(ctx, "member")))
                );
    }

    private static int executeCommand(CommandSource command, ServerPlayerEntity member) {
        ServerPlayerEntity player = UtilsCommands.getServerPlayerEntity(command);

        if(player == null) {
            return 0;
        }

        command.sendFeedback(new StringTextComponent(ClaimManager.addMember(player, member.getName().getString())), true);

        return 1;
    }
}
