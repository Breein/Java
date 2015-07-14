package Java;

import Debug.Debug;
import World.WorldCell;

public class Eat {
    private int id, x, y;
    private int amount = 100;
    private String type = "Eat";
    private WorldCell[][] map;

    public Eat(int id, int x, int y, WorldCell[][] map){
        this.id = id;
        this.x = x;
        this.y = y;
        this.map = map;

        this.map[this.y][this.x].beHere("eat_" + id, this.id, "Eats");
    }

    public String Type(){
        return type;
    }
    public int X(){
        return x;
    }
    public int Y(){
        return y;
    }
    public int Id(){
        return id;
    }
    public int Amount(){
        return amount;
    }
    public void doEat(){
        amount = amount - 10;
    }
}
