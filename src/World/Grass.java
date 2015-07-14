package World;

public class Grass extends Territory{
    public Grass() {
        super("grass", GRASS[6], GRASS[1], GRASS[0]);
        generate();
    }

    @Override
    protected void generate(){
        for(int i = 0; i < GRASS[1]; i++){
            data[i] = new ObjectDimensions(rnd(1, WORLD_Y), rnd(1, WORLD_X), rnd(GRASS[4], GRASS[5]), rnd(GRASS[2], GRASS[3]));
        }
    }
}
