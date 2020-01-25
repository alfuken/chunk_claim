package lime.chunk_claim;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class ClaimManager {
    static final String S_NO_PERMISSION       = "chunk_claim.claim_manager.no_permission";
    static final String S_Y_R_OWNER           = "chunk_claim.claim_manager.y_r_owner";
    static final String S_Y_R_NOT_OWNER       = "chunk_claim.claim_manager.y_r_not_owner";
    static final String S_TAKEN               = "chunk_claim.claim_manager.taken";
    static final String S_CLAIMED             = "chunk_claim.claim_manager.claimed";
    static final String S_UNCLAIMED           = "chunk_claim.claim_manager.unclaimed";
    static final String S_NOT_CLAIMED         = "chunk_claim.claim_manager.not_claimed";
    static final String S_NO_LONGER_CLAIMED   = "chunk_claim.claim_manager.no_longer_claimed";
    static final String S_EVICT_OK            = "chunk_claim.claim_manager.evict_ok";
    static final String S_IS_ALREADY_A_MEMBER = "chunk_claim.claim_manager.is_already_a_member";
    static final String S_MEMBER_ADDED        = "chunk_claim.claim_manager.member_added";
    static final String S_IS_NOT_A_MEMBER     = "chunk_claim.claim_manager.is_not_a_member";
    static final String S_MEMBER_REMOVED      = "chunk_claim.claim_manager.member_removed";
    static final String S_NO_CLAIMED_CHUNKS   = "chunk_claim.claim_manager.no_claimed_chunks";
    static final String S_Y_CLAIMED           = "chunk_claim.claim_manager.y_claimed";
    static final String S_OWNED_BY            = "chunk_claim.claim_manager.owned_by";
    static final String S_MEMBERS             = "chunk_claim.claim_manager.members";
    static final String S_CHUNK_LOC           = "%nx: %d, z: %d";
    static final String S_MAX_CLAIMS          = "chunk_claim.claim_manager.max_claims";
    static final String S_OOPS                = "chunk_claim.claim_manager.oops";

    public static void protect(BlockPos pos, int dim)
    {
        ClaimData cd = new ClaimData();
        cd.setPos(pos);
        cd.setDimension(dim);
        cd.save();
    }

    public static TextComponentTranslation claim(EntityPlayer player)
    {
        ClaimData cd = ClaimData.get(player);

        if (cd.isOwner(player)) return new TextComponentTranslation(S_Y_R_OWNER);
        if (cd.isOwned()) return new TextComponentTranslation(S_TAKEN);
        if (ClaimData.getClaimsCount(player) >= ChunkClaim.max_chunks) return new TextComponentTranslation(S_MAX_CLAIMS, ChunkClaim.max_chunks);

        (new ClaimData(player)).save();

        return new TextComponentTranslation(S_CLAIMED);
    }

    public static TextComponentTranslation unclaim(EntityPlayer player)
    {
        ClaimData cd = ClaimData.get(player);

        if (!cd.isOwner(player)) return new TextComponentTranslation(S_Y_R_NOT_OWNER);

        cd.delete();
        return new TextComponentTranslation(S_UNCLAIMED);
    }

    public static TextComponentTranslation sudo_unclaim(EntityPlayer player)
    {
        if (!player.isCreative()) return new TextComponentTranslation(S_NO_PERMISSION);

        ClaimData cd = ClaimData.get(player);

        if (!cd.isOwned()) return new TextComponentTranslation(S_NOT_CLAIMED);

        cd.delete();
        return new TextComponentTranslation(S_NO_LONGER_CLAIMED);
    }

    public static TextComponentTranslation evict(EntityPlayer player)
    {
        ClaimData cd = ClaimData.get(player);

        if (!cd.isCitizen(player)) return new TextComponentTranslation(S_NO_PERMISSION);

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

        return new TextComponentTranslation(S_EVICT_OK);
    }

    public static TextComponentTranslation addMember(EntityPlayer player, String name)
    {
        ClaimData cd = ClaimData.get(player);

        if (!cd.isOwner(player)) return new TextComponentTranslation(S_Y_R_NOT_OWNER);
        if (cd.isMember(name)) return new TextComponentTranslation(S_IS_ALREADY_A_MEMBER, name);

//        ClaimData.remove(cd);
        if (cd.addMember(name)) return new TextComponentTranslation(S_MEMBER_ADDED, name);
        else return new TextComponentTranslation(S_OOPS);
//        ClaimData.add(cd);

    }

    public static TextComponentTranslation removeMember(EntityPlayer player, String name)
    {
        ClaimData cd = ClaimData.get(player);

        if (!cd.isOwner(player)) return new TextComponentTranslation(S_Y_R_NOT_OWNER);
        if (!cd.isMember(name)) return new TextComponentTranslation(S_IS_NOT_A_MEMBER, name);

//        ClaimData.remove(cd);
        if (cd.removeMember(name)) return new TextComponentTranslation(S_MEMBER_REMOVED, name);
        else return new TextComponentTranslation(S_OOPS);
//        ClaimData.add(cd);

    }

    public static TextComponentTranslation list(EntityPlayer player)
    {
        int cnt = ClaimData.getClaimsCount(player);
        if (cnt == 0) return new TextComponentTranslation(S_NO_CLAIMED_CHUNKS);

        StringBuilder s = new StringBuilder();

        for (ClaimData cd : ClaimData.getClaims(player))
        {
            s.append(String.format(S_CHUNK_LOC, cd.getX()+8, cd.getZ()+8));
        }

        return new TextComponentTranslation(S_Y_CLAIMED, cnt, ChunkClaim.max_chunks, s.toString());
    }

    public static List<TextComponentTranslation> info(EntityPlayer player)
    {
        ClaimData cd = ClaimData.get(player);

        if (!cd.isOwned()) return Arrays.asList(new TextComponentTranslation(S_NOT_CLAIMED));

        if (cd.isOwner(player))
        {
            List<TextComponentTranslation> t = Arrays.asList(new TextComponentTranslation(S_Y_R_OWNER));

            if (cd.hasMembers())
            {
                t.add(new TextComponentTranslation(S_MEMBERS, String.join(", ", cd.getMembers())));
            }

            return t;
        }
        else
        {
            return Arrays.asList(new TextComponentTranslation(S_OWNED_BY, cd.getOwner()));
        }
    }
}
