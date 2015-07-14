package World;

public class World implements Dimensions{
    private int x, y, poly;

    public World(){
        this.x = WORLD_X;
        this.y = WORLD_Y;
        this.poly = POLYSIZE;
    }

    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }
    public void setPoly(int poly){
        this.poly = poly;
        LOG.setLog("Polygon size: ", this.poly + "");
    }

    public int x(){
        return x;
    }
    public int y(){
        return y;
    }
    public int poly(){
        return poly;
    }
}
