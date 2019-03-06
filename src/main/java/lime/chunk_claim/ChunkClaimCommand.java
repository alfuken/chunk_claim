package lime.chunk_claim;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

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

        String result;

        switch (command_name){
            case "info":         result = ClaimManager.info(player); break;
            case "list":         result = ClaimManager.list(player); break;
            case "claim":        result = ClaimManager.claim(player); break;
            case "unclaim":      result = ClaimManager.unclaim(player); break;
            case "addmember":    result = ClaimManager.addMember(player, argument1); break;
            case "removemember": result = ClaimManager.removeMember(player, argument1); break;
            case "evict":        result = ClaimManager.evict(player); break;
            case "sudo_unclaim": result = ClaimManager.sudo_unclaim(player); break;
            default:             result = getUsage(sender);
        }

        sender.sendMessage(new TextComponentString(result));
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
