/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Java;

import World.ContentWorldCell;
import World.Dimensions;
import World.World;
import World.WorldCell;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Mobs implements Runnable, Direction, Dimensions, ActionListener{
    private Thread thread = new Thread(this);
    private Timer mainTimer = new Timer(1000, this);

    // флаг для работы временного усыпления
    private boolean suspendFlag = false;
    // флаг для полной остановки потока
    private boolean stop = false;
    
    private int id, x, y;
    private position moved = new position();
    private position movedPos = new position();

    private int live = 100;
    private int hunger = 0;
    private int speed = 100;
    private String name;
    private String gender;
    private WorldCell target;
    private WorldCell[][] map;
    private HashMap<String, Eat> eats;

    private HashMap<Integer, AStarOpened> opened = new HashMap<Integer, AStarOpened>();
    private HashMap<Integer, AStarOpened> ignored = new HashMap<Integer, AStarOpened>();
    private ArrayList<cellRealPatch> real_patch = new ArrayList<cellRealPatch>();
    private HashMap<String, contentInSight> inSight = new HashMap<String, contentInSight>();
    
    // установка приоритета потока, как много он будет требовать
    // ресурсов(точнее сказать как часто) у проца
    // относительно других потоков в программе
    void setPriority(int priority){ thread.setPriority(priority); }

    // метод приостановки потока
    synchronized void suspend(){ suspendFlag = true; }

    // возобновение работы после приостановки
    synchronized void resume(){ suspendFlag = false; notify(); }

    // умертвление насовсем
    void stop(){ stop = true; }

    // запуск потока(можно этот метод запускать в конструкторе), тогда он сразу начинает работать вызываея метод run()
    void start(){ thread.start(); }

    // остановка потока на заданный промежуток времени, сюда 1 секунду
    
    public Mobs(int id, int x, int y, String name, String gender, WorldCell target, WorldCell[][] map, HashMap<String, Eat> eats){
        this.id = id;
        this.x = x;  // координата "х" в мире
        this.y = y;  // координата "у" в мире
        this.name = name;
        this.gender = gender;
        this.target = target;
        this.map = map;
        this.eats = eats;
        this.map[y][x].beHere(this.name, this.id, "Mobs");

        speed = map[y][x].price();

        movedPos.x = 0;
        movedPos.y = 0;
        moved.x = 0;
        moved.y = 0;

        thread.setName(this.name);
        find_patch(false);
        start();
        mainTimer.start();
    }

    public int gmx(){
        return x * WORLD.poly() + movedPos.x;
    }
    public int gmy(){
        return y * WORLD.poly() + movedPos.y;
    }

    public int X(){
        return x;
    }
    public int Y(){
        return y;
    }
    public String gName(){
        return name;
    }
    public String gGender(){
        return gender;
    }
    public int gHunger(){
        return hunger;
    }
    /*
    public void sTarget(String target){
        this.target = target;
    }
    */
    public WorldCell gTarget(){
        return target;
    }

    public ArrayList<cellRealPatch> Patch(){
        return real_patch;
    }

    public void find_patch(boolean type){

        int active_pid = 0;
        int pid, X, Y;
        String pType;

        if(!type){

            preparePatch();

            pid = map[y][x].id();
            pType = map[y][x].type();

            if(opened.get(pid) == null && ignored.get(pid) == null && !pType.equals("wall")){
                opened.put(pid, new AStarOpened(0, x, y, 0, 0, 0));
            }

            find_patch(true);

        }else{

            // Проверяем открытый список, если в нем есть хоть один елемент, продолжаем поиск пути
            // Если он пуст, значит путь не найден
            if(opened.size() > 0){ // Открытые клетки - есть

                int t_x = target.x();
                int t_y = target.y();
                int t_id = target.id();

                if(opened.get(t_id) == null){

                    // Ищем в открытом списке клетку с наименьшей стоимостью F. Делаем ее текущей клеткой.

                    int f = 100000;

                    for(Map.Entry<Integer, AStarOpened> open : opened.entrySet()){
                        if(open.getValue().F() < f){
                            active_pid = open.getKey();
                            f = open.getValue().F();
                        }
                    }

                    pid = active_pid;

                    X = opened.get(pid).X();
                    Y = opened.get(pid).Y();

                    // Помещаем ее в закрытый список. (И удаляем с открытого)

                    ignored.put(pid, opened.get(pid));
                    opened.remove(pid);

                    // Для каждой из соседних 8-ми клеток ...

                    for(int i = -1; i <= 1; i++){
                        for(int j = -1; j <= 1; j++){

                            int px = X + j;
                            int py = Y + i;
                            int t_pid = map[py][px].id();
                            String ptype = map[py][px].type();


                            // Если клетка непроходимая или она находится в закрытом списке, игнорируем ее. В противном случае делаем следующее

                            if(ignored.get(t_pid) == null && !ptype.equals("wall")){

                                // Учимся не срезать углы, при обходе препятсвий.

                                boolean diagonally = true;
                                boolean direct = (j == 0 || i == 0) ? false : true;

                                if(direct){
                                    if(i == -1 && j == -1){
                                        if(map[py + 1][px].type().equals("wall") || map[py][px + 1].type().equals("wall")){
                                            diagonally = false;
                                        }
                                    }

                                    if(i == -1 && j == 1){
                                        if(map[py + 1][px].type().equals("wall") || map[py][px - 1].type().equals("wall")){
                                            diagonally = false;
                                        }
                                    }

                                    if(i == 1 && j == -1){
                                        if(map[py - 1][px].type().equals("wall") || map[py][px + 1].type().equals("wall")){
                                            diagonally = false;
                                        }
                                    }

                                    if(i == 1 && j == 1){
                                        if(map[py][px-1].type().equals("wall") || map[py-1][px].type().equals("wall")){
                                            diagonally = false;
                                        }
                                    }
                                }

                                ///Если клетка еще не в открытом списке, то добавляем ее туда. Делаем текущую клетку родительской для это клетки. Расчитываем стоимости F, G и H клетки.
                                if(!direct || diagonally){

                                    if(opened.get(t_pid) == null){

                                        int parent = pid;
                                        int h = (Math.abs(t_x - px) + Math.abs(t_y - py)) * 10;
                                        int g = (j == 0 || i == 0) ? 10 : 14;

                                        g = g + ignored.get(pid).G() + map[py][px].price();
                                        f = h + g;

                                        opened.put(t_pid, new AStarOpened(parent, px, py, h, g, f));

                                        //Если клетка уже в открытом списке, то проверяем, не дешевле ли будет путь через эту клетку.
                                        //Для сравнения используем стоимость G. Более низкая стоимость G указывает на то, что путь будет дешевле.
                                        //Эсли это так, то меняем родителя клетки на текущую клетку и пересчитываем для нее стоимости G и F.
                                        //Если вы сортируете открытый список по стоимости F, то вам надо отсортировать свесь список в соответствии с изменениями.

                                    }else{

                                        int g1 = (j == 0 || i == 0) ? 10 : 14; g1 = g1 + ignored.get(pid).G() + map[py][px].price();
                                        int g2 = opened.get(t_pid).G();

                                        if(g1 < g2){
                                            int h = opened.get(t_pid).H();
                                                f = h + g1;

                                            opened.put(t_pid, new AStarOpened(pid, px, py, h, g1, f));
                                        }
                                    }
                                }
                            }
                        }
                    }

                    find_patch(true);

                }else{

                    real_patch.add(new cellRealPatch(t_id, opened.get(t_id).X(), opened.get(t_id).Y()));

                    int start = map[y][x].id();
                    int target = opened.get(t_id).parent();

                    while(target != start){
                        real_patch.add(new cellRealPatch(target, 0, 0));
                        target = ignored.get(target).parent();
                    }

                    real_patch = reverse(real_patch);

                    for(int ind = 0, len = real_patch.size(); ind < len; ind++){
                        pid = real_patch.get(ind).ID();

                        if(ignored.get(pid) != null){
                            real_patch.get(ind).x(ignored.get(pid).X());
                            real_patch.get(ind).y(ignored.get(pid).Y());
                        }else{
                            real_patch.get(ind).x(opened.get(pid).X());
                            real_patch.get(ind).y(opened.get(pid).Y());
                        }
                    }
                }

            }else{
                System.out.println("Patch is null, " + name);
                real_patch.clear();
                opened.clear();
                ignored.clear();
            }

        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ArrayList<cellRealPatch> reverse(ArrayList<cellRealPatch> real_patch){
        ArrayList<cellRealPatch> temp = new ArrayList<cellRealPatch>();

        for(int i = real_patch.size() - 1; i > -1; i--){
            temp.add(real_patch.get(i));
        }
        return temp;
    }

    private void preparePatch(){
        real_patch.clear();
        opened.clear();
        ignored.clear();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public HashMap<String, contentInSight> see(){
        seeInSight(x, y, 10, true, 0, LEFT);
        return inSight;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean seeInSight(int xx, int yy, int stop, boolean key, int index, int side){
        int nxy = 0;

        for(int i = 0, len = 8 - index; i < len; i++){

            if(map[yy][xx] != null && !map[yy][xx].type().equals("wall")){
                if(key){
                    add_inSight(map[yy][xx].content(), xx, yy);
                }else{
                    if(i != 0){
                        add_inSight(map[yy][xx].content(), xx, yy);
                    }
                }
            }else{
                if(i == 0){
                    if(key){
                        return seeInSight(x, y, 10, false, 0, side);
                    }else{
                        return false;
                    }
                }else{
                    break;
                }
            }

            if(side == LEFT || side == RIGHT){
                xx = side == LEFT ? xx - 1 : xx + 1;
                yy = key ? yy + 1 : yy - 1;
            }else{
                yy = side == UP ? yy - 1 : yy + 1;
                xx = key ? xx + 1 : xx - 1;
            }
        }

        if(index < stop){
            index++;
            if(side == LEFT || side == RIGHT){
                nxy = side == LEFT ? x - index : x + index;
                seeInSight(nxy, y, stop, key, index, side);
            }else{
                nxy = side == UP ? y - index : y + index;
                seeInSight(x, nxy, stop, key, index, side);
            }
        }else{
            if(key){
                seeInSight(x, y, 10, false, 0, side);
            }
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void add_inSight(HashMap<String, ContentWorldCell> content, int xx, int yy){
        if(content.size() > 0){
            for(ContentWorldCell objects : content.values()){
                if(objects.Type().equals("Eats") && eats.get("eat_" + objects.ID()).Amount() > 0){
                    inSight.put(objects.Type() + "_" + objects.ID(), new contentInSight(objects.ID(), objects.Type()));
                }else{
                    if(x != xx || y != yy){
                        inSight.put("cell_" + map[yy][xx].id(), new contentInSight(map[yy][xx].id(), "Cell"));
                    }
                }
            }
        }else{
            if(x != xx || y != yy){
                inSight.put("cell_" + map[yy][xx].id(), new contentInSight(map[yy][xx].id(), "Cell"));
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void moveToCell(){
        map[y][x].outHere(name);

        x = real_patch.get(0).X();
        y = real_patch.get(0).Y();

        movedPos.x = 0;
        movedPos.y = 0;
        moved.x = 0;
        moved.y = 0;

        map[y][x].beHere(name, id, "Mobs");

        real_patch.remove(0);
    }

    private void moveToPatch(){
        World world = WORLD;
        int poly = world.poly();

        if(real_patch.size() > 0){
            int tx = real_patch.get(0).X();
            int ty = real_patch.get(0).Y();

            speed = map[ty][tx].price();

            if(x == tx){
                moved.x = poly;
            }
            if(y == ty){
                moved.y = poly;
            }

            if(poly <= moved.x && poly <= moved.y){
                moveToCell();
            }else{
                if(poly != moved.x) {
                    movedPos.x = x > tx ? movedPos.x - 1 : movedPos.x + 1;
                    moved.x++;
                }
                if(poly != moved.y) {
                    movedPos.y = y > ty ? movedPos.y - 1 : movedPos.y + 1;
                    moved.y++;
                }
            }
            //LOG.setLog("x : y", movedPos.x + " : " + movedPos.y + " | " + moved.x + " : " + moved.y);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        hunger++;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void run() {
        try{
            // В этом цикле будут вычислятся шаги моба
            while (!stop){
                // конструкция для работы временного усыпления моба
                synchronized (this){
                    while(suspendFlag){
                        wait();
                    }
                }
                // место для описания действия моба
                // ====================================

                moveToPatch();
                //System.out.println(speed);

                // ====================================
                Thread.sleep(speed); // спать n м.секунд
                //LOG.setLog("Tread: ", speed + "");
            }
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }
}

class AStarOpened{
    private int x, y = 0;
    private int H, G, F = 0;
    private int parent;

    AStarOpened(int parent, int x, int y, int H, int G, int F){
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.F = F;
        this.G = G;
        this.H = H;
    }

    public int H(){
        return H;
    }
    public int F(){
        return F;
    }
    public int G(){
        return G;
    }
    public int parent(){
        return parent;
    }

    public int X(){
        return x;
    }
    public int Y(){
        return y;
    }

}

class cellRealPatch{
    private int id, x, y;

    cellRealPatch(int id, int x, int y){
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int ID(){
        return id;
    }
    public void x(int x){
        this.x = x;
    }
    public void y(int y){
        this.y = y;
    }
    public int X(){
        return x;
    }
    public int Y(){
        return y;
    }
}

class contentInSight extends ContentWorldCell{
    contentInSight(int id, String type){
        super(id, type);
    }
}

class position{
    public int x = 0;
    public int y = 0;
}
    
