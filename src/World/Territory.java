package World;

import java.util.Random;

abstract class Territory implements Dimensions {
    protected ObjectDimensions data[];
    protected int price, count, codeType;
    protected String style;
            
    private Random r = new Random();
    
    
    Territory(String style, int price, int count, int codeType){
        this.price = price;
        this.style = style;
        this.count = count;
        this.codeType = codeType;

        data = new ObjectDimensions[count];
    }
    
    public String gStyle(){
        return style;
    }
    public int gPrice(){
        return price;
    }
    public ObjectDimensions[] gData(){
        return data;
    }
    
    public String print(int index){
        return data[index].y() + ":" + data[index].x() + " - " + data[index].sy() + ":" + data[index].sx();
    }
    
    public int y(int index){
        return data[index].y();
    }
    public int x(int index){
        return data[index].x();
    }
    public int sy(int index){
        return data[index].sy();
    }
    public int sx(int index){
        return data[index].sx();
    }
    public int gCount(){
        return count;
    }
    public int gCodeType(){
        return codeType;
    }
    
    public int rnd(int min, int max){
        return r.nextInt(max - min) + min;
    }
    
    abstract protected void generate();
    
}

class ObjectDimensions {
    private int x;
    private int y;
    private int sx;
    private int sy;
    
    ObjectDimensions(int y, int x, int sy, int sx){
        this.y = y;
        this.x = x;
        this.sy = sy;
        this.sx = sx;
    }
    
    public int y(){
        return y;
    }
    public int x(){
        return x;
    }
    public int sy(){
        return sy;
    }
    public int sx(){
        return sx;
    }
}