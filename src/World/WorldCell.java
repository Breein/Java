
package World;

import java.util.HashMap;

import Java.Mobs;

public class WorldCell implements Dimensions{
    private int id, x, y, wx, wy, price, codeType;
    private String type;
    private HashMap<String, ContentWorldCell> content = new HashMap<String, ContentWorldCell>();
    private HashMap<String, Mobs> mobs;

    public WorldCell(int id, int x, int y, String type, int price){
        this.id = id;
        this.x = x;
        this.y = y;
        this.type = type;
        this.price = price;
        this.codeType = -1;

        wx = x * POLYSIZE;
        wy = y * POLYSIZE;
    }
    
    public void setType(String type){
        this.type = type;
    }
    public void setPrice(int price){
        this.price = price;
    }
    public void setCodeType(int codeType){
        this.codeType = codeType;
    }
    
    public String type(){
        return type;
    }
    public int id(){
        return id;
    }
    public int price(){
        return price;
    }
    public int codeType(){
        return codeType;
    }

    public int x(){
        return x;
    }
    public int y(){
        return y;
    }
    public int wx(){
        return wx;
    }
    public int wy(){
        return wy;
    }

    public void beHere(String name, int id, String type){
        //System.out.println(name + ":" +  id + ":" + type);
        content.put(name, new ContentWorldCell(id, type));
    }
    public void outHere(String name){
        content.remove(name);
    }
    public void whoHere(){
        System.out.print("Content in cell[" + y + ":" + x + "]: ");

        for(ContentWorldCell objects : content.values()){
            if(objects.Type().equals("Mobs")){
                System.out.print("Mob:" + mobs.get("mobs_" + objects.ID()).gName() + ", ");
            }else{

            }

        }
        System.out.println("");
    }

    public HashMap<String, ContentWorldCell> content(){
        return content;
    }
}
