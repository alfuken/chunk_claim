package lime.chunk_claim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

class ClaimData { // for alternative way: implements Serializable
    private static Map<String, ClaimData> data = new HashMap<>();

    static ClaimData get(int x, int z, int dimension)
    {
        return data.getOrDefault(getKey(x,z,dimension), new ClaimData());
    }

    static String getKey(int x, int z, int dimension){
        return x+":"+"z"+"@"+dimension;
    }

    static ClaimData get(int x, int z, EntityPlayer player)
    {
        return get(x, z, player.dimension);
    }


    static ClaimData get(BlockPos pos, EntityPlayer player)
    {
        return get(pos.getX() >> 4, pos.getZ() >> 4, player.dimension);
    }

    static ClaimData get(BlockPos pos, int dimension)
    {
        return get(pos.getX() >> 4, pos.getZ() >> 4, dimension);
    }

    static ClaimData get(Entity entity)
    {
        return get(entity.getPosition().getX() >> 4, entity.getPosition().getZ() >> 4, entity.dimension);
    }

    static void add(ClaimData cd){
        data.put(cd.key(), cd);
        save_all();
    }

    static void remove(ClaimData cd){
        data.remove(cd.key());
        save_all();
    }

    static void load()
    {
        File f = new File("data/"+ChunkClaim.MODID+".json");
        if ( f.exists() )
        {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
                Gson gson = new Gson();
                Type listType = new TypeToken<Collection<ClaimData>>() {}.getType();
                data = gson.fromJson(bufferedReader, listType);
            } catch (Exception e) { e.printStackTrace(); }

            // Alternative way:
            // ObjectInputStream obj_in = new ObjectInputStream (new FileInputStream("data/"+ChunkClaim.MODID+".db"));
            // data = (List<ChunkData>)obj_in.readObject();
            // obj_in.close();
        }
    }

    static void save_all()
    {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting().serializeNulls();
        Gson gson = builder.create();

        File f = new File("data/");
        if ( !f.exists() ) f.mkdir();

        f = new File("data/"+ChunkClaim.MODID+".json");
        if (f.exists()) f.delete();

        try {
            FileWriter writer = new FileWriter("data/"+ChunkClaim.MODID+".json");
            writer.write(gson.toJson(data));
            writer.close();
        } catch (Exception e) { e.printStackTrace(); }

        // Alternative way:
        // ObjectOutputStream obj_out = new ObjectOutputStream(new FileOutputStream("data/"+ChunkClaim.MODID+".db"));
        // obj_out.writeObject( data );
        // obj_out.close();
    }

    static int getClaimsCount(EntityPlayer player)
    {
        return getClaims(player).size();
    }

    static List<ClaimData> getClaims(EntityPlayer player)
    {
        return data.values().stream().filter(cd -> cd.isOwner(player)).collect(Collectors.toList());
    }

    // Instance methods

    private int x;
    private int z;
    private int dimension;
    private String owner;
    private Set<String> members = new HashSet<>();

    ClaimData()
    {
        this.x = Integer.MAX_VALUE;
        this.z = Integer.MAX_VALUE;
        this.dimension = Integer.MAX_VALUE;
        this.owner = "";
    }

    ClaimData(EntityPlayer player)
    {
        this.x = player.getPosition().getX() >> 4;
        this.z = player.getPosition().getZ() >> 4;
        this.dimension = player.dimension;
        this.owner = player.getDisplayNameString();
    }

    void save(){
        add(this);
    }

    void delete(){
        remove(this);
    }

    String key(){
        return getKey(x,z,dimension);
    }

    void setX(int x)
    {
        this.x = x;
    }

    int getX()
    {
        return this.x;
    }

    void setZ(int z)
    {
        this.z = z;
    }

    int getZ()
    {
        return this.z;
    }

    void setDimension(int dimension)
    {
        this.dimension = dimension;
    }

    int getDimension()
    {
        return this.dimension;
    }

    void setOwner(String playername)
    {
        this.owner = playername;
    }

    String getOwner()
    {
        return this.owner;
    }

    boolean addMember(String name)
    {
        if (!isMember(name))
        {
            boolean result = this.members.add(name);
            save();
            return result;
        }
        return false;
    }

    boolean removeMember(String name)
    {
        if (isMember(name))
        {
            boolean result = this.members.remove(name);
            save();
            return result;
        }
        return false;
    }

    Set<String> getMembers()
    {
        return this.members;
    }

    boolean hasMembers()
    {
        return !this.members.isEmpty();
    }

    boolean isOwned()
    {
        return !this.owner.equals("");
    }

    boolean isOwner(String name)
    {
        return this.owner.equalsIgnoreCase(name);
    }

    boolean isOwner(EntityPlayer player)
    {
        return isOwner(player.getDisplayNameString());
    }

    boolean isMember(EntityPlayer player)
    {
        return isMember(player.getDisplayNameString());
    }

    boolean isMember(String name)
    {
        return this.members.contains(name);
    }

    boolean isCitizen(EntityPlayer player)
    {
        boolean is_fake_and_enabled = player instanceof FakePlayer && !ChunkClaim.disable_fake_block_interaction;
        return (this.isOwner(player) || this.isMember(player) || is_fake_and_enabled);
//        if (this.isOwner(player)) return true;
//        else return this.isMember(player.getDisplayNameString());
    }


}