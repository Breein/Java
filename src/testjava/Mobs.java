/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testjava;

import Debug.Debug;
import World.ContentWorldCell;
import World.Dimensions;
import World.WorldCell;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Mobs implements Runnable, Direction, Dimensions, ActionListener{
    private Thread thread = new Thread(this);
    private Timer mainTimer = new Timer(1000, this);

    private Debug dg;

    // флаг для работы временного усыпления
    private boolean suspendFlag = false;
    // флаг для полной остановки потока
    private boolean stop = false;
    
    private int id, x, y, wx, wy;
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

    //private HashMap<String, >
    
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
    
    public Mobs(int id, int x, int y, String name, String gender, WorldCell target, WorldCell[][] map, HashMap<String, Eat> eats, Debug debug){
        this.id = id;
        this.x = x;  // координата "х" в мире
        this.y = y;  // координата "у" в мире
        this.name = name;
        this.gender = gender;
        this.target = target;
        this.map = map;
        this.eats = eats;
        this.map[y][x].beHere(this.name, this.id, "Mobs");

        this.dg = debug;

        speed = map[y][x].gPrice();

        wx = x * POLYSIZE; // координата "х" в окне, при отрисовке
        wy = y * POLYSIZE; // координата "у" в окне, при отрисовке
        
        thread.setName(this.name);
        find_patch(false);
        start();
        mainTimer.start();
    }

    public int x(){
        return wx;
    }
    public int y(){
        return wy;
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

    public ArrayList<cellRealPatch> gPatch(){
        return real_patch;
    }

    public void find_patch(boolean type){

        int active_pid = 0;
        int pid, X, Y;
        String pType;

        if(!type){

            preparePatch();

            pid = map[y][x].gId();
            pType = map[y][x].gType();

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
                int t_id = target.gId();

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
                            int t_pid = map[py][px].gId();
                            String ptype = map[py][px].gType();


                            // Если клетка непроходимая или она находится в закрытом списке, игнорируем ее. В противном случае делаем следующее

                            if(ignored.get(t_pid) == null && !ptype.equals("wall")){

                                // Учимся не срезать углы, при обходе препятсвий.

                                boolean diagonally = true;
                                boolean direct = (j == 0 || i == 0) ? false : true;

                                if(direct){
                                    if(i == -1 && j == -1){
                                        if(map[py + 1][px].gType().equals("wall") || map[py][px + 1].gType().equals("wall")){
                                            diagonally = false;
                                        }
                                    }

                                    if(i == -1 && j == 1){
                                        if(map[py + 1][px].gType().equals("wall") || map[py][px - 1].gType().equals("wall")){
                                            diagonally = false;
                                        }
                                    }

                                    if(i == 1 && j == -1){
                                        if(map[py - 1][px].gType().equals("wall") || map[py][px + 1].gType().equals("wall")){
                                            diagonally = false;
                                        }
                                    }

                                    if(i == 1 && j == 1){
                                        if(map[py][px-1].gType().equals("wall") || map[py-1][px].gType().equals("wall")){
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

                                        g = g + ignored.get(pid).G() + map[py][px].gPrice();
                                        f = h + g;

                                        opened.put(t_pid, new AStarOpened(parent, px, py, h, g, f));

                                        //Если клетка уже в открытом списке, то проверяем, не дешевле ли будет путь через эту клетку.
                                        //Для сравнения используем стоимость G. Более низкая стоимость G указывает на то, что путь будет дешевле.
                                        //Эсли это так, то меняем родителя клетки на текущую клетку и пересчитываем для нее стоимости G и F.
                                        //Если вы сортируете открытый список по стоимости F, то вам надо отсортировать свесь список в соответствии с изменениями.

                                    }else{

                                        int g1 = (j == 0 || i == 0) ? 10 : 14; g1 = g1 + ignored.get(pid).G() + map[py][px].gPrice();
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

                    int start = map[y][x].gId();
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

            if(map[yy][xx] != null && !map[yy][xx].gType().equals("wall")){
                if(key){
                    add_inSight(map[yy][xx].gContent(), xx, yy);
                }else{
                    if(i != 0){
                        add_inSight(map[yy][xx].gContent(), xx, yy);
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
                        inSight.put("cell_" + map[yy][xx].gId(), new contentInSight(map[yy][xx].gId(), "Cell"));
                    }
                }
            }
        }else{
            if(x != xx || y != yy){
                inSight.put("cell_" + map[yy][xx].gId(), new contentInSight(map[yy][xx].gId(), "Cell"));
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void moveToCell(){
        map[y][x].outHere(name);

        x = real_patch.get(0).X();
        y = real_patch.get(0).Y();

        map[y][x].beHere(name, id, "Mobs");

        real_patch.remove(0);
    }

    private void moveToPatch(){
        if(real_patch.size() > 0){
            int _wx = real_patch.get(0).X() * POLYSIZE;//map[real_patch.get(0).Y()][real_patch.get(0).X()].wx();
            int _wy = real_patch.get(0).Y() * POLYSIZE;//map[real_patch.get(0).Y()][real_patch.get(0).X()].wy();

            speed = map[real_patch.get(0).Y()][real_patch.get(0).X()].gPrice();

            dg.setLog("Mob[" + id + "] speed", speed + "");

            if(wx == _wx && wy == _wy){
                moveToCell();
            }else{
                if(wx != _wx){
                    wx = wx > _wx ? wx - 1 : wx + 1;
                }
                if(wy != _wy){
                    wy = wy > _wy ? wy - 1 : wy + 1;
                }
            }
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
    
