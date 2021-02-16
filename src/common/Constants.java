package common;


public class Constants {
    public static final int NUM_PLAYERS = 2;
    public static final int NUM_NEUTRALS = 4;
    public static final int NUM_PLAYERS_PLUS_NEUTRALS = NUM_PLAYERS + NUM_NEUTRALS;
    public static final int NUM_COUNTRIES = 42;
    public static final int INIT_COUNTRIES_PLAYER = 9;
    public static final int INIT_COUNTRIES_NEUTRAL = 6;
    public static final int INIT_UNITS_PLAYER = 36;
    public static final int INIT_UNITS_NEUTRAL = 24;
    public static final int NUM_MAIN_CARDS = 42;
    public static final int NUM_MAIN_CARDS_PLUS_WILDCARDS = 44;
    public static final int NUM_INDIVIDUAL_CARDS = 14;
    public static final int NUM_WILDCARDS = 2;

    public static final String[] COUNTRY_NAMES = {
            "Ontario", "Quebec", "NW Territory", "Alberta", "Greenland", "E United States", "W United States", "Central America", "Alaska",
            "Great Britain", "W Europe", "S Europe", "Ukraine", "N Europe", "Iceland", "Scandinavia",
            "Afghanistan", "India", "Middle East", "Japan", "Ural", "Yakutsk", "Kamchatka", "Siam", "Irkutsk", "Siberia", "Mongolia", "China",
            "E Australia", "New Guinea", "W Australia", "Indonesia",
            "Venezuela", "Peru", "Brazil", "Argentina",
            "Congo", "N Africa", "S Africa", "Egypt", "E Africa", "Madagascar"};  // for reference

    public static final int[][] ADJACENT = {
            {4, 1, 5, 6, 3, 2},    // 0
            {4, 5, 0},
            {4, 0, 3, 8},
            {2, 0, 6, 8},
            {14, 1, 0, 2},
            {0, 1, 7, 6},
            {3, 0, 5, 7},
            {6, 5, 32},
            {2, 3, 22},
            {14, 15, 13, 10},
            {9, 13, 11, 37},     // 10
            {13, 12, 18, 39, 10},
            {20, 16, 18, 11, 13, 15},
            {15, 12, 11, 10, 9},
            {15, 9, 4},
            {12, 13, 14},
            {20, 27, 17, 18, 12},
            {16, 27, 23, 18},
            {12, 16, 17, 40, 39, 11},
            {26, 22},
            {25, 27, 16, 12},    // 20
            {22, 24, 25},
            {8, 19, 26, 24, 21},
            {27, 31, 17},
            {21, 22, 26, 25},
            {21, 24, 26, 27, 20},
            {24, 22, 19, 27, 25},
            {26, 23, 17, 16, 20, 25},
            {29, 30},
            {28, 30, 31},
            {29, 28, 31},      // 30
            {23, 29, 30},
            {7, 34, 33},
            {32, 34, 35},
            {32, 37, 35, 33},
            {33, 34},
            {37, 40, 38},
            {10, 11, 39, 40, 36, 34},
            {36, 40, 41},
            {11, 18, 40, 37},
            {39, 18, 41, 38, 36, 37},  //40
            {38, 40}
    };
    public static final int NUM_CONTINENTS = 6;
    public static final String[] CONTINENT_NAMES = {"N_America", "Europe", "Asia", "Australia", "S_America", "Africa"};  // for reference
    public static final int[] CONTINENT_IDS = {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5};
    public static final int[] CONTINENT_VALUES = {5, 5, 7, 2, 2, 3};

    public static final int MAP_WIDTH = 1000;    // must be even
    public static final int MAP_HEIGHT = 600;
    public static final int SHELL_WIDTH = 200;
    public static final int COUNTRY_NODE_RADIUS = 10;
    public static final int CONTINENT_PANE_WIDTH = 140;
    public static final int CONTINENT_PANE_HEIGHT = 180;

    public static final int[][] COUNTRY_COORDS = {
            {180, 115},     // 0
            {248, 123}, //
            {146, 81}, //
            {116, 112}, //
            {350, 52}, //
            {188, 173}, //
            {116, 173}, //
            {143, 263}, //
            {65, 84}, //
            {422, 120},//
            {422, 170},  //    // 10
            {497, 153}, //
            {547, 115}, //
            {475, 129}, //
            {395, 89},//
            {470, 94}, //
            {628, 163}, //
            {678, 235}, //
            {562, 205}, //
            {845, 192}, //
            {618, 105},  //    // 20
            {760, 90}, //
            {857, 85}, //
            {751, 257}, //
            {750, 120}, //
            {673, 100}, //
            {760, 153}, //
            {733, 200}, //
            {878, 428}, //
            {884, 348}, //
            {825, 424}, //      // 30
            {795, 328}, //
            {208, 301}, //
            {207, 372}, //
            {276, 370}, //
            {231, 470}, //
            {500, 340},
            {440, 253}, //
            {508, 418}, //
            {516, 227}, //
            {550, 306},        // 40
            {578, 401} //
    };

    // Working progress
    public static class Notifications {
        public static final String WELCOME = "Welcome to Risk -\nBy Team7\n";
        public static final String NAME = "What is your name? ";
        public static final String MOVE = "it is your turn.";
        public static final String OPTION = "What move would you like to make?";
        public static final String GG = "Good game!";
        public static final String DRAWN = " drew the card ";
        public static final String TERRITORY = "The cards for the territories will now be drawn.";
        public static final String TERRITORY_OPTION = "Would you like to see the card draw outcome before initialization?";
    }

    public static class ComponentIds {
        public static final String MAP = "map-component";
        public static final String NEUTRAL_PLAYER = "neutral-player";
        public static final String SHELL = "shell-component";
        public static final String SHELL_INPUT = "input-line";
        public static final String SHELL_LOG = "log";
        public static final String GAME_ROOT = "game-root";
        public static final String TEXT = "text";
    }

    public static class Paths {
        public static final String STYLE_SHEET = "resources/style.css";
        public static final String ICON = "resources/swords.png";
    }

}