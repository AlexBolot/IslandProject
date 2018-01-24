package fr.unice.polytech.si3.qgl.ise.enums;

public class DroneEnums
{
    public enum ZQSD
    {
        LEFT,
        RIGHT,
        FRONT,
        BACK
    }

    public enum NSEW
    {
        EAST("E"),
        WEST("W"),
        NORTH("N"),
        SOUTH("S");

        private String value;

        NSEW (String value)
        {
            this.value = value;
        }

        public String getValue ()
        {
            return value;
        }
    }

    public enum Obstacle
    {
        GROUND,
        BORDER
    }

    public enum SubState
    {
        INIT_ECHO_FRONT,
        INIT_ECHO_RIGHT,
        INIT_ECHO_LEFT,

        SEARCH_CHOSE_DIR,
        SEARCH_ECHO_SIDE,
        SEARCH_FLY,

        REACH_ISLAND_TURN1,
        REACH_ISLAND_TURN2,
        REACH_ISLAND_TURN3,
        REACH_ISLAND_ECHO_FRONT,
        REACH_ISLAND_MOVE,

        SCAN_STEP_1,
        SCAN_STEP_2,

        CHANGELINE_TURN1,
        CHANGELINE_TURN2,
        CHANGELINE_ECHO_FRONT,
        CHANGELINE_DONE
    }
}
