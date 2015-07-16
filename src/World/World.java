package World;

public class World implements Dimensions{
    private int x, y, poly, sx, sy;

    public World(){
        this.x = WORLD_X;
        this.y = WORLD_Y;
        this.sx = 0;
        this.sy = 0;
        this.poly = POLYSIZE;
    }

    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }

    public void setSX(int sx){
        this.sx = x;
    }
    public void setSY(int sy){
        this.sy = y;
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
    public int sx(){
        return sx;
    }
    public int sy(){
        return sy;
    }
    public int poly(){
        return poly;
    }
}
