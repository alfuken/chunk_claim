package lime.chunk_claim.commands;

import lime.chunk_claim.ClaimData;
import lime.chunk_claim.Configuration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.ChunkPos;

import java.util.Random;

class ClaimManager {
    static final String S_NO_PERMISSION       = "You don't have sufficient permissions to do that.";
    static final String S_Y_R_OWNER           = "You are the owner this chunk.";
    static final String S_Y_R_NOT_OWNER       = "You are not the owner of this chunk.";
    static final String S_TAKEN               = "Someone else already owns this chunk.";
    static final String S_CLAIMED             = "You have claimed this chunk.";
    static final String S_UNCLAIMED           = "You no longer own this chunk.";
    static final String S_NOT_CLAIMED         = "This chunk is not claimed.";
    static final String S_NO_LONGER_CLAIMED   = "This chunk is no longer claimed.";
    static final String S_EVICT_OK            = "All intruders (if any) were removed from this chunk. Some of them may die. Oh well ¯\\_(ツ)_/¯";
    static final String S_IS_ALREADY_A_MEMBER = "%s is already in a member list.";
    static final String S_MEMBER_ADDED        = "%s has been added to the member list.";
    static final String S_IS_NOT_A_MEMBER     = "%s is not in a member list.";
    static final String S_MEMBER_REMOVED      = "%s has been removed from the member list.";
    static final String S_NO_CLAIMED_CHUNKS   = "You have no claimed chunks.";
    static final String S_Y_CLAIMED           = "You have claimed %d chunks out of allowed %d:";
    static final String S_OWNED_BY            = "This chunk is owned by %s";
    static final String S_MEMBERS             = " Registered members: %s";
    static final String S_CHUNK_LOC           = "%nx: %d, z: %d";
    static final String S_MAX_CLAIMS          = "You have reached max allowed number (%d) of claimed chunks.";
    static final String S_OOPS = "Oops. Something went wrong. Please contact server owner or author of this mod.";

    static String claim(PlayerEntity player)
    {
        ClaimData cd = ClaimData.get(player);

        if (cd.isOwner(player)) return S_Y_R_OWNER;
        if (cd.isOwned()) return S_TAKEN;
        if (ClaimData.getClaimsCount(player) >= Configuration.COMMON.max_claimed_chunks.get()) return String.format(S_MAX_CLAIMS, Configuration.COMMON.max_claimed_chunks.get());

        (new ClaimData(player)).save();

        return S_CLAIMED;
    }

    static String unclaim(PlayerEntity player)
    {
        ClaimData cd = ClaimData.get(player);

        if (!cd.isOwner(player)) return S_Y_R_NOT_OWNER;

        cd.delete();
        return S_UNCLAIMED;
    }

    static String sudo_unclaim(PlayerEntity player)
    {
        if (!player.isCreative()) return S_NO_PERMISSION;

        ClaimData cd = ClaimData.get(player);

        if (!cd.isOwned()) return S_NOT_CLAIMED;

        cd.delete();
        return S_NO_LONGER_CLAIMED;
    }

    static String evict(PlayerEntity player)
    {
        ClaimData cd = ClaimData.get(player);

        if (!cd.isCitizen(player)) return S_NO_PERMISSION;

        Random r = new Random();

        ChunkPos actor_cp = new ChunkPos(player.getPosition());

        /*for (PlayerEntity other_player : player.getEntityWorld().playerEntities)
        {
            ChunkPos cp = new ChunkPos(other_player.getPosition());

            if (cp.equals(actor_cp) && !cd.isCitizen(other_player))
            {
                Chunk c = player.getEntityWorld().getChunk(player.getPosition().getX(), player.getPosition().getZ());
                int y = c.getHeightValue(player.getPosition().getX(), player.getPosition().getZ());
                other_player.setPositionAndUpdate(other_player.posX + (r.nextBoolean() ? 16 : -16), y+5, other_player.posZ + (r.nextBoolean() ? 16 : -16));
            }
        }

        return S_EVICT_OK;*/
        return "";
    }

    static String addMember(PlayerEntity player, String name)
    {
        ClaimData cd = ClaimData.get(player);

        if (!cd.isOwner(player)) return S_Y_R_NOT_OWNER;
        if (cd.isMember(name)) return String.format(S_IS_ALREADY_A_MEMBER, name);

//        ClaimData.remove(cd);
        if (cd.addMember(name)) return String.format(S_MEMBER_ADDED, name);
        else return S_OOPS;
//        ClaimData.add(cd);

    }

    static String removeMember(PlayerEntity player, String name)
    {
        ClaimData cd = ClaimData.get(player);

        if (!cd.isOwner(player)) return S_Y_R_NOT_OWNER;
        if (!cd.isMember(name)) return String.format(S_IS_NOT_A_MEMBER, name);

//        ClaimData.remove(cd);
        if (cd.removeMember(name)) return String.format(S_MEMBER_REMOVED, name);
        else return S_OOPS;
//        ClaimData.add(cd);

    }

    static String list(PlayerEntity player)
    {
        int cnt = ClaimData.getClaimsCount(player);
        if (cnt == 0) return S_NO_CLAIMED_CHUNKS;

        StringBuilder s = new StringBuilder(String.format(S_Y_CLAIMED, cnt, Configuration.COMMON.max_claimed_chunks.get()));

        for (ClaimData cd : ClaimData.getClaims(player))
        {
            s.append(String.format(S_CHUNK_LOC, cd.getX()+8, cd.getZ()+8));
        }

        return s.toString();
    }

    static String info(PlayerEntity player)
    {
        ClaimData cd = ClaimData.get(player);

        if (!cd.isOwned()) return S_NOT_CLAIMED;

        if (cd.isOwner(player))
        {
            StringBuilder s = new StringBuilder();

            s.append(S_Y_R_OWNER);

            if (cd.hasMembers())
            {
                s.append(String.format(S_MEMBERS, String.join(", ", cd.getMembers())));
            }

            return s.toString();
        }
        else
        {
            return String.format(S_OWNED_BY, cd.getOwner());
        }
    }
}
