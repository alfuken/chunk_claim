package lime.chunk_claim;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

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
    static final String S_MAX_CLAIMS          = "You have reached maximally allowed number (%d) of claimed chunks.";
    static final String S_OOPS = "Oops. Something went wrong. Please contact server owner or author of this mod.";

    static String claim(EntityPlayer player)
    {
        ClaimData cd = ClaimData.get(player);

        if (cd.isOwner(player)) return S_Y_R_OWNER;
        if (cd.isOwned()) return S_TAKEN;
        if (ClaimData.getClaimsCount(player) >= ChunkClaim.max_chunks) return String.format(S_MAX_CLAIMS, ChunkClaim.max_chunks);

        cd.setX(player.getPosition().getX() >> 4);
        cd.setZ(player.getPosition().getZ() >> 4);
        cd.setOwner(player.getDisplayNameString());
        cd.setDimension(player.dimension);

        if (ClaimData.add(cd)) return S_CLAIMED;
        else return S_OOPS;
    }

    static String unclaim(EntityPlayer player)
    {
        ClaimData cd = ClaimData.get(player);

        if (!cd.isOwner(player)) return S_Y_R_NOT_OWNER;

        if (ClaimData.remove(cd)) return S_UNCLAIMED;
        else return S_OOPS;
    }

    static String sudo_unclaim(EntityPlayer player)
    {
        if (!player.isCreative()) return S_NO_PERMISSION;

        ClaimData cd = ClaimData.get(player);

        if (!cd.isOwned()) return S_NOT_CLAIMED;

        if (ClaimData.remove(cd)) return S_NO_LONGER_CLAIMED;
        else return S_OOPS;
    }

    static String evict(EntityPlayer player)
    {
        ClaimData cd = ClaimData.get(player);

        if (!cd.isCitizen(player)) return S_NO_PERMISSION;

        Random r = new Random();

        ChunkPos actor_cp = new ChunkPos(player.getPosition());

        for (EntityPlayer other_player : player.getEntityWorld().playerEntities)
        {
            ChunkPos cp = new ChunkPos(other_player.getPosition());

            if (cp.equals(actor_cp) && !cd.isCitizen(other_player))
            {
                Chunk c = player.getEntityWorld().getChunkFromBlockCoords(player.getPosition());
                int y = c.getHeightValue(player.getPosition().getX(), player.getPosition().getZ());
                other_player.setPositionAndUpdate(other_player.posX + (r.nextBoolean() ? 16 : -16), y+5, other_player.posZ + (r.nextBoolean() ? 16 : -16));
            }
        }

        return S_EVICT_OK;
    }

    static String addMember(EntityPlayer player, String name)
    {
        ClaimData cd = ClaimData.get(player);

        if (!cd.isOwner(player)) return S_Y_R_NOT_OWNER;
        if (cd.isMember(name)) return String.format(S_IS_ALREADY_A_MEMBER, name);

//        ClaimData.remove(cd);
        if (cd.addMember(name)) return String.format(S_MEMBER_ADDED, name);
        else return S_OOPS;
//        ClaimData.add(cd);

    }

    static String removeMember(EntityPlayer player, String name)
    {
        ClaimData cd = ClaimData.get(player);

        if (!cd.isOwner(player)) return S_Y_R_NOT_OWNER;
        if (!cd.isMember(name)) return String.format(S_IS_NOT_A_MEMBER, name);

//        ClaimData.remove(cd);
        if (cd.removeMember(name)) return String.format(S_MEMBER_REMOVED, name);
        else return S_OOPS;
//        ClaimData.add(cd);

    }

    static String list(EntityPlayer player)
    {
        int cnt = ClaimData.getClaimsCount(player);
        if (cnt == 0) return S_NO_CLAIMED_CHUNKS;

        StringBuilder s = new StringBuilder(String.format(S_Y_CLAIMED, cnt, ChunkClaim.max_chunks));

        for (ClaimData cd : ClaimData.getClaims(player))
        {
            s.append(String.format(S_CHUNK_LOC, cd.getX()+8, cd.getZ()+8));
        }

        return s.toString();
    }

    static String info(EntityPlayer player)
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
