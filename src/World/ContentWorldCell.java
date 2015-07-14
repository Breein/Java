package World;

public class ContentWorldCell{
    private int id;
    private String type;

    public ContentWorldCell(int id, String type){
        this.id = id;
        this.type = type;
    }
    public int ID(){
        return id;
    }
    public String Type(){
        return type;
    }
}
