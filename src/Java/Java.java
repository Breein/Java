/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Java;


import Debug.*;
import World.*;
import javafx.scene.control.Cell;

import java.awt.*;

import java.awt.event.*;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;

///////////////////////////////////////////////////////////


//==============================================================================

class RenderWorld extends JPanel implements ActionListener, Dimensions{
    private HashMap<String, Mobs> mobs;
    private HashMap<String, Eat> eats;
    private WorldCell[][] map;
    private World world;
    private sensePlane sp;
    private Debug dg;


    private ArrayList<cellRealPatch> patch;

    Timer mainTimer = new Timer(40, this);

    public RenderWorld(WorldCell[][] map, HashMap<String, Mobs> mobs, HashMap<String, Eat> eats, sensePlane sp){
        this.map = map;
        this.mobs = mobs;
        this.eats = eats;
        this.sp = sp;
        this.world = WORLD;

        mainTimer.start();
    }

    @Override
    public void paint(Graphics g) {

        g.setColor(Color.white);
        g.fillRect(0, 0, WORLD_X * POLYSIZE, WORLD_Y * POLYSIZE);

        for (int y = 0; y < world.y(); y++) {
            for (int x = 0; x < world.x(); x++) {
                switch (map[y][x].codeType()) {
                    case 0:
                        g.setColor(new Color(0, 0, 0));
                        break;

                    case 1:
                        g.setColor(new Color(151, 223, 108));
                        break;

                    case 2:
                        g.setColor(new Color(250, 238, 54));
                        break;

                    case 3:
                        g.setColor(new Color(113, 163, 109));
                        break;

                    default:
                        g.setColor(new Color(232, 232, 232));
                        break;
                }
                g.fillRect(x * world.poly(), y * world.poly(), world.poly(), world.poly());
            }
        }

        for (Mobs mob : mobs.values()) {
            int x, y, xx, yy;
            ArrayList<cellRealPatch> patch;

            x = mob.gmx();
            y = mob.gmy();

            g.setColor(Color.blue);
            g.fillRect(mob.gTarget().x() * world.poly(), mob.gTarget().y() * world.poly(), world.poly(), world.poly());

            /*
            g.setColor(new Color(157, 44, 44));
            g.fillRect(mob.X() * world.poly(), mob.Y() * world.poly(), world.poly(), world.poly());
            */

            g.setColor(Color.cyan);
            g.fillRect(x, y, world.poly(), world.poly());
            g.setColor(Color.black);
            g.drawRect(x, y, world.poly(), world.poly());
            g.drawString(mob.gHunger() + "", x, y + ((world.poly() / 2) + 4));

            /*
            patch = mob.Patch();
            if(patch.size() > 0) {
                xx = patch.get(0).X();
                yy = patch.get(0).Y();

                g.setColor(Color.magenta);
                g.fillRect(xx * world.poly(), yy * world.poly(), world.poly(), world.poly());
            }
            */
        }

        for (Eat eat : eats.values()) {
            g.setColor(Color.red);
            g.fillRect(eat.X() * world.poly(), eat.Y() * world.poly(), world.poly(), world.poly());
            g.setColor(Color.white);
            g.drawString(eat.Amount() + "", eat.X() * world.poly(), (eat.Y() * world.poly()) + ((world.poly() / 2) + 4));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        repaint();
        sp.scrollMap(this);
    }
}

class sensePlane extends JPanel implements Dimensions, MouseMotionListener, MouseWheelListener{
    private int mx = 0, my = 0;
    private World worldInfo;
    private int poly;
    private RenderWorld renPlane;
    private int windowSize[] = {0, 0, WORLD_X * POLYSIZE, WORLD_Y * POLYSIZE, (WORLD_X * POLYSIZE) - 20, (WORLD_Y * POLYSIZE) - 20};

    sensePlane(){
        this.worldInfo = WORLD;
        this.poly = worldInfo.poly();

        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    public void setRenPlane(RenderWorld renPlane){
        this.renPlane = renPlane;
    }

    public void scrollMap(RenderWorld object){
        Rectangle bounds = object.getBounds();

        if(mx < 20 && bounds.x < windowSize[0]){
            object.setLocation(bounds.x + 9, bounds.y);
        }else if(mx > windowSize[4] && (bounds.width + bounds.x) > windowSize[2]){
            object.setLocation(bounds.x - 9, bounds.y);
        }

        if(my < 20 && bounds.y < windowSize[1]){
            object.setLocation(bounds.x, bounds.y + 9);
        }else if(my > windowSize[5] && (bounds.height + bounds.y) > windowSize[3]){
            object.setLocation(bounds.x, bounds.y - 9);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mx = e.getX();
        my = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int mouse = e.getWheelRotation();
        boolean key = false;

        if(mouse > 0){
            poly = poly - 1; // уменьшить размер тайла
            poly = poly < POLYSIZE ? POLYSIZE : poly;       // если он меньше минимального, то ставим минимальный
            renPlane.setSize(WORLD_X * poly, WORLD_Y * poly);   // установка размеров по новому тайлу

            int x, y, w, h; Rectangle bounds = renPlane.getBounds();    // переменные и получение размера нового полотна (это формально будущий размер, а не текущий)

            x = bounds.x + (WORLD_X / 2);   // сдвигаем слой по х, на половину клуток рамера мира по Х, таким сдвигом, мы сдвигаем пропорцинально, и в лево и вправо
            y = bounds.y + (WORLD_Y / 2);   // также по у
            w = bounds.width + x;           // расчет координаты нижнего угла, "конечный-Х"
            h = bounds.height + y;          // полуем "конечный-У"

            // Блок для углов, правый верхний, левый нижний.
            if(w <= windowSize[2] && y >= 0){ //  "конечный-Х" меньше чем размер окна (карта вылезла в видимость) и при этом, по "у" тоже вылезла (правый верхний угол)
                x = x + (windowSize[2] - w);  // вернуть "конечный-Х" в координаты окна
                key = true;                   // ключ что мы прошли блок углов
            }
            if(h <= windowSize[3] && x >= 0){ // "конечный-У" меньше чем размер окна (карта вылезла в видимость) и при этом, по "х" тоже вылезла (левый нижний угол)
                y = y + (windowSize[3] - h);  // вернуть "конечный-У" в координаты окна
                key = true;                   // ключ что мы прошли блок углов
            }

            // Блок для касания одной стороны, он же будет и при нижнем правом углу, сработают оба условия
            if(w <= windowSize[2] && !key){  // если вылез только "конечный-Х", и при этом, мы не были в блоке углов
                x = x + (windowSize[2] - w); // вернуть "конечный-Х" в координаты окна
            }
            if(h <= windowSize[3] && !key){  // если вылез только "конечный-У", и при этом, мы не были в блоке углов
                y = y + (windowSize[3] - h); // вернуть "конечный-У" в координаты окна
            }

            // Блок универсальный
            if(x >= 0){  // если "х" вылез за 0, вернуть "х" в 0
                x = 0;
            }
            if(y >= 0){ // если "у" вылез за 0, вернуть "у" в 0
                y = 0;
            }

            renPlane.setLocation(x, y); // установить получившиеся координаты сдвига слою

            worldInfo.setSX(x);
            worldInfo.setSY(y);
        }else{ //////////////////////////////////////
            int x, y; Rectangle bounds = renPlane.getBounds();

            x = e.getX() / (windowSize[2] / WORLD_X); // вычилсяем на сколько двигать слой, по координате мыши Х
            y = e.getY() / (windowSize[3] / WORLD_Y); // по координате мыши У

            x = bounds.x - x;
            y = bounds.y - y;

            poly = poly + 1;

            renPlane.setSize(WORLD_X * poly, WORLD_Y * poly);
            renPlane.setLocation(x, y);

            worldInfo.setSX(x);
            worldInfo.setSY(y);
        }

        worldInfo.setPoly(poly);
    }
}



//==============================================================================

public class Java implements Dimensions {
    
    public static void main(String[] args) {

        World worldInfo = new World();

        WorldCell[][] map;

        HashMap<String, Mobs> mobs;
        HashMap<String, Eat> eats;

        Debug debug = new Debug();
        Generator generator = new Generator();
         
        map = generator.createMap();
        generator.addTerritory(new Grass(), map);
        generator.addTerritory(new Sand(), map);
        generator.addTerritory(new Swamp(), map);
        generator.addTerritory(new Wall(), map);

        eats = generator.createEats(1, map);
        mobs = generator.createMobs(5, map, eats);

        //mobs.get("mob_0").seeInSight(mobs.get("mob_0").gX(), mobs.get("mob_0").gY(), 10, true, 0, Direction.LEFT);
        //

        int size[] = {WORLD_X * POLYSIZE, (WORLD_Y + 1) * POLYSIZE, 0, 0};
        Rectangle userWindow = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        size[2] = (userWindow.width / 2) - (size[0] / 2);
        size[3] = (userWindow.height / 2) - (size[1] / 2) - 100;

        JFrame window = new JFrame("World 0.1");
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, size[0], size[1]);

        sensePlane sp = new sensePlane();
        sp.setBounds(0, 0, size[0], size[1]);
        sp.setOpaque(false);

        RenderWorld rw = new RenderWorld(map, mobs, eats, sp);
        sp.setRenPlane(rw);
        rw.setBounds(0, 0, size[0], size[1]);
        rw.setOpaque(true);

        layeredPane.add(rw, 0);
        layeredPane.add(sp, 1);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocation(size[2], size[3]);
        window.setSize(size[0], size[1]);
        window.setResizable(false);

        window.add(layeredPane);
        window.setVisible(true);

        ////////////////////////////////////////////////////////////////

        JFrame debugWindow = new JFrame("Debug");
        JLayeredPane debugLayeredPane = new JLayeredPane();
        debugLayeredPane.setBounds(0, 0, size[0], 200);

        RenderDebug rd = new RenderDebug();
        rd.setBounds(0, 0, size[0], 200);
        rd.setOpaque(true);

        debugLayeredPane.add(rd, 0);

        debugWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        debugWindow.setLocation(size[2], size[3] + size[1]);
        debugWindow.setSize(size[0], 200);
        debugWindow.setResizable(false);

        debugWindow.add(debugLayeredPane);
        debugWindow.setVisible(true);
    }
}
