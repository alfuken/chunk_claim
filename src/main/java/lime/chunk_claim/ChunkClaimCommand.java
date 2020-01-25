package lime.chunk_claim;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ChunkClaimCommand implements ICommand {
    private final List<String> aliases;

    ChunkClaimCommand(){
        aliases = new ArrayList<>();
        aliases.add("chunkclaim");
        aliases.add("cclaim");
    }

    @Override
    public String getName(){
        return "chunk_claim";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "<chunk_claim|chunkclaim|cclaim> <info|list|claim|unclaim|addmember <name>|removemember <name>|evict>";
        // |allow <rule>|forbid <rule>>, where <rule> is one of the following: devices_blocks, block_use, block_break, block_place, use, spawn, attack, activation
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) return;
        if (sender.getEntityWorld().isRemote)  return;

        EntityPlayer player = (EntityPlayer)sender;

        String command_name = "";
        String argument1 = "";

        if (args.length >= 1){
            command_name = args[0];
            if (args.length >= 2){
                argument1 = args[1];
            }
        }

        if ((command_name.equals("addmember") || command_name.equals("removemember")) && argument1.equals("")){
            command_name = "";
        }

        switch (command_name){
            case "info":         for (TextComponentTranslation t : ClaimManager.info(player)) { player.sendMessage(t); }; break;
            case "list":         player.sendMessage(ClaimManager.list(player)); break;
            case "claim":        player.sendMessage(ClaimManager.claim(player)); break;
            case "unclaim":      player.sendMessage(ClaimManager.unclaim(player)); break;
            case "addmember":    player.sendMessage(ClaimManager.addMember(player, argument1)); break;
            case "removemember": player.sendMessage(ClaimManager.removeMember(player, argument1)); break;
            case "evict":        player.sendMessage(ClaimManager.evict(player)); break;
            case "sudo_unclaim": player.sendMessage(ClaimManager.sudo_unclaim(player)); break;
            default:             player.sendMessage(new TextComponentString(getUsage(player)));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

}
