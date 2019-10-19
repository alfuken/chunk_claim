package lime.chunk_claim;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class UtilsCommands {
    public static ServerPlayerEntity getServerPlayerEntity(CommandSource command) {
        ServerPlayerEntity player = null;

        try {
            player = command.asPlayer();
        } catch (CommandSyntaxException e) {
            command.sendErrorMessage(new StringTextComponent("Invalid syntax").applyTextStyle(TextFormatting.RED));
        }

        return player;
    }
}
