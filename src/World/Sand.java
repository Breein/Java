package World;

public class Sand extends Territory{
    public Sand() {
        super("sand", SAND[6], SAND[1], SAND[0]);
        generate();
    }

    @Override
    protected void generate(){
        for(int i = 0; i < SAND[1]; i++){
            data[i] = new ObjectDimensions(rnd(1, WORLD_Y), rnd(1, WORLD_X), rnd(SAND[4], SAND[5]), rnd(SAND[2], SAND[3]));
        }
    }
}
