package World;

import java.util.HashMap;
import java.util.Random;

import Debug.Debug;
import testjava.Eat;
import testjava.Mobs;

public class Generator implements Dimensions {
    
    private Random r = new Random();
    private Debug debug;

    public Generator(Debug debug){
        this.debug = debug;
    }
    
    public WorldCell[][] createMap(){
        int id = 0;
        WorldCell map[][] = new WorldCell[WORLD_Y][WORLD_X];

        for(int y = 0; y < WORLD_Y; y++){
            for(int x = 0; x < WORLD_X; x++){
                map[y][x] = new WorldCell(id, x, y, "0", 150);
                id++;
            }
        }
        
        return map;
    }
    
    public void addTerritory(Territory ob, WorldCell[][] map){
        for(int k = 0; k < ob.gCount(); k++){
            for(int i = ob.y(k), len = ob.y(k) + ob.sy(k); i < len; i++){
                for(int j = ob.x(k), lenj = ob.x(k) + ob.sx(k); j < lenj; j++){
                    try{
                      map[i][j].sType(ob.gStyle());
                      map[i][j].sPrice(ob.gPrice());
                      map[i][j].sCodeType(ob.gCodeType());
                    }catch(ArrayIndexOutOfBoundsException error){}
                }
            }
        }
    }
    
    public HashMap<String, Mobs> createMobs(int count, WorldCell[][] map, HashMap<String, Eat> eats){
        HashMap<String, Mobs> allMobs = new HashMap<String, Mobs>();
        WorldCell target;
        int x, y;

        for(int i = 0; i < count; i++){

            do{
                x = rnd(1, WORLD_X - 1);
                y = rnd(1, WORLD_Y - 1);
            }while(map[y][x].gType().equals("wall"));

            do{
                target = map[rnd(1, WORLD_Y - 1)][rnd(1, WORLD_X - 1)];
            }while(target.gType().equals("wall"));

            allMobs.put("mob_" + i, new Mobs(i, x, y, "mob_" + i, "men", target, map, eats, debug));
        }
        
        return allMobs;
    }

    public HashMap<String, Eat> createEats(int count, WorldCell[][] map){
        HashMap<String, Eat> allEats = new HashMap<String, Eat>();
        int x, y;

        for(int i = 0; i < count; i++){

            do{
                x = rnd(1, WORLD_X - 1);
                y = rnd(1, WORLD_Y - 1);
            }while(map[y][x].gType().equals("wall"));

            allEats.put("eat_" + i, new Eat(i, x, y, map, debug));
        }

        return allEats;
    }

    private int rnd(int min, int max){
        return r.nextInt(max - min) + min;
    }
}
