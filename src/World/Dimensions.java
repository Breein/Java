package World;

import Debug.Debug;

public interface Dimensions {
    int WORLD_X = 40;
    int WORLD_Y = 30;

    // | числовой ключ типа | количество | мин. Х | макс. Х | мин. У | макс. У |
    int WAll[] =    {0, 100, 1, 3, 1, 3, 10000};
    int GRASS[] =   {1, 40,  2, 6, 1, 8, 75};
    int SAND[] =    {2, 10,  2, 5, 4, 8, 230};
    int SWAMP[] =   {3, 4,   5, 8, 3, 10, 370};

    int POLYSIZE = 22;

    World WORLD = new World();
    Debug LOG = new Debug();
}
