package lime.chunk_claim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

class ClaimData { // for alternative way: implements Serializable
    private static List<ClaimData> data = new ArrayList<>();

    static ClaimData get(int x, int z, int dimension)
    {
        for (ClaimData cd : data)
        {
            if (cd.getX() == x && cd.getZ() == z && cd.getDimension() == dimension)
            {
                return cd;
            }
        }
        return new ClaimData();
    }

    static ClaimData get(int x, int z, EntityPlayer player)
    {
        return get(x, z, player.dimension);
    }

    static ClaimData get(EntityPlayer player)
    {
        return get(player.getPosition().getX() >> 4, player.getPosition().getZ() >> 4, player.dimension);
    }

    static boolean add(ClaimData cd){
        boolean result = data.add(cd);
        save();
        return result;
    }

    static boolean remove(ClaimData cd){
        boolean result = data.remove(cd);
        save();
        return result;
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

    static void save()
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
        int n = 0;
        for (ClaimData cd : data){
            if (cd.isOwner(player)) n += 1;
        }
        return n;
    }

    static List<ClaimData> getClaims(EntityPlayer player)
    {
        ArrayList<ClaimData> list = new ArrayList<>();
        for (ClaimData cd : data){
            if (cd.isOwner(player)) list.add(cd);
        }
        return list;
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
        if (!this.members.contains(name))
        {
            boolean result = this.members.add(name);
            save();
            return result;
        }
        return false;
    }

    boolean removeMember(String name)
    {
        if (this.members.contains(name))
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
        for (String member : this.members)
        {
            if (member.equalsIgnoreCase(name))
            {
                return true;
            }
        }
        return false;
    }

    boolean isCitizen(EntityPlayer player)
    {
        return (this.isOwner(player) || this.isMember(player));
//        if (this.isOwner(player)) return true;
//        else return this.isMember(player.getDisplayNameString());
    }


}