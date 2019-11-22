package lime.chunk_claim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.FakePlayer;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ClaimData { // for alternative way: implements Serializable
    private static Map<String, ClaimData> data = new HashMap<>();

    public static ClaimData get(int x, int z, int dimension) {
        return data.getOrDefault(getKey(x, z, dimension), new ClaimData());
    }

    public static String getKey(int x, int z, int dimension) {
        return x + ":" + z + "@" + dimension;
    }

    static ClaimData get(int x, int z, PlayerEntity player) {
        return get(x, z, player.dimension.getId());
    }

    static ClaimData get(BlockPos pos, PlayerEntity player) {
        return get(pos.getX() >> 4, pos.getZ() >> 4, player.dimension.getId());
    }

    static ClaimData get(BlockPos pos, int dimension) {
        return get(pos.getX() >> 4, pos.getZ() >> 4, dimension);
    }

    public static ClaimData get(Entity entity) {
        return get(entity.getPosition().getX() >> 4, entity.getPosition().getZ() >> 4, entity.dimension.getId());
    }

    static void add(ClaimData cd) {
        data.put(cd.key(), cd);
        save_all();
    }

    static void remove(ClaimData cd) {
        data.remove(cd.key());
        save_all();
    }

    static void load() {
        File f = new File("data/" + ChunkClaim.MOD_ID + ".json");
        if (f.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
                Gson gson = new Gson();
                Type listType = new TypeToken<Map<String, ClaimData>>() {}.getType();
                data = gson.fromJson(bufferedReader, listType);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Alternative way:
            // ObjectInputStream obj_in = new ObjectInputStream (new FileInputStream("data/"+ChunkClaim.MODID+".db"));
            // data = (List<ChunkData>)obj_in.readObject();
            // obj_in.close();
        }
    }

    static void save_all() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting().serializeNulls();
        Gson gson = builder.create();

        File f = new File("data/");
        if (!f.exists()) f.mkdir();

        f = new File("data/" + ChunkClaim.MOD_ID + ".json");
        if (f.exists()) f.delete();

        try {
            FileWriter writer = new FileWriter("data/" + ChunkClaim.MOD_ID + ".json");
            writer.write(gson.toJson(data));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Alternative way:
        // ObjectOutputStream obj_out = new ObjectOutputStream(new FileOutputStream("data/"+ChunkClaim.MODID+".db"));
        // obj_out.writeObject( data );
        // obj_out.close();
    }

    public static int getClaimsCount(PlayerEntity player) {
        return getClaims(player).size();
    }

    public static List<ClaimData> getClaims(PlayerEntity player) {
        return data.values().stream().filter(cd -> cd.isOwner(player)).collect(Collectors.toList());
    }

    // Instance methods

    private int x;
    private int z;
    private int dimension;
    private String owner;
    private Set<String> members = new HashSet<>();

    ClaimData() {
        this.x = Integer.MAX_VALUE;
        this.z = Integer.MAX_VALUE;
        this.dimension = Integer.MAX_VALUE;
        this.owner = "";
    }

    public ClaimData(PlayerEntity player) {
        this.x = player.getPosition().getX() >> 4;
        this.z = player.getPosition().getZ() >> 4;
        this.dimension = player.dimension.getId();
        this.owner = player.getDisplayName().getString();
    }

    public void save() {
        add(this);
    }

    public void delete() {
        remove(this);
    }

    String key() {
        return getKey(x, z, dimension);
    }

    void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return this.x;
    }

    void setZ(int z) {
        this.z = z;
    }

    public int getZ() {
        return this.z;
    }

    void setDimension(DimensionType dimension) {
        this.dimension = dimension.getId();
    }

    int getDimension() {
        return this.dimension;
    }

    void setOwner(String playername) {
        this.owner = playername;
    }

    public String getOwner() {
        return this.owner;
    }

    public boolean addMember(String name) {
        if (!isMember(name)) {
            boolean result = this.members.add(name);
            save();
            return result;
        }
        return false;
    }

    public boolean removeMember(String name) {
        if (isMember(name)) {
            boolean result = this.members.remove(name);
            save();
            return result;
        }
        return false;
    }

    public Set<String> getMembers() {
        return this.members;
    }

    public boolean hasMembers() {
        return !this.members.isEmpty();
    }

    public boolean isOwned() {
        return !this.owner.isEmpty();
    }

    boolean isOwner(String name) {
        return this.owner.equalsIgnoreCase(name);
    }

    public boolean isOwner(PlayerEntity player) {
        return isOwner(player.getDisplayName().getString());
    }

    boolean isMember(PlayerEntity player) {
        return isMember(player.getDisplayName().getString());
    }

    public boolean isMember(String name) {
        return this.members.contains(name);
    }

    public boolean isCitizen(PlayerEntity player) {
        boolean is_fake_and_enabled = player instanceof FakePlayer && !Configuration.COMMON.disable_fake_block_interaction.get();
        return (this.isOwner(player) || this.isMember(player) || is_fake_and_enabled);
//        if (this.isOwner(player)) return true;
//        else return this.isMember(player.getDisplayNameString());
    }


}