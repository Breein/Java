package World;

public class Wall extends Territory{

    public Wall() {
        super("wall", WAll[6], WAll[1], WAll[0]);
        generate();
    }
    
    @Override
    protected void generate(){
        data[0] = new ObjectDimensions(0, 0, 1, WORLD_X);
        data[1] = new ObjectDimensions(WORLD_Y - 1, 0, 1, WORLD_X);
        data[2] = new ObjectDimensions(0, 0, WORLD_Y, 1);
        data[3] = new ObjectDimensions(0, WORLD_X - 1, WORLD_Y, 1);
        
        for(int i = 4; i < WAll[1]; i++){
            data[i] = new ObjectDimensions(rnd(1, WORLD_Y), rnd(1, WORLD_X), rnd(WAll[4], WAll[5]), rnd(WAll[2], WAll[3]));
        }
    }
}
