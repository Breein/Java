package World;

public class Swamp extends Territory{
    public Swamp() {
        super("swamp", SWAMP[6], SWAMP[1], SWAMP[0]);
        generate();
    }

    @Override
    protected void generate(){
        for(int i = 0; i < SWAMP[1]; i++){
            data[i] = new ObjectDimensions(rnd(1, WORLD_Y), rnd(1, WORLD_X), rnd(SWAMP[4], SWAMP[5]), rnd(SWAMP[2], SWAMP[3]));
        }
    }
}
