package fr.unice.polytech.si3.qgl.ise.enums;

import java.util.Arrays;

public class DroneEnums {
    public enum ZQSD {
        LEFT,
        RIGHT,
        FRONT,
        BACK;

        public static ZQSD getOpposite(ZQSD dir) {
            switch (dir) {
                case LEFT:
                    return RIGHT;
                case RIGHT:
                    return LEFT;
                case FRONT:
                    return BACK;
                case BACK:
                    return FRONT;
            }

            throw new IllegalArgumentException("Wrong params");
        }
    }

    public enum NSEW {
        EAST("E"),
        WEST("W"),
        NORTH("N"),
        SOUTH("S");

        private String value;

        static {
            NORTH.toTheRight = EAST;
            EAST.toTheRight = SOUTH;
            SOUTH.toTheRight = WEST;
            WEST.toTheRight = NORTH;

            NORTH.toTheLeft = WEST;
            WEST.toTheLeft = SOUTH;
            SOUTH.toTheLeft = EAST;
            EAST.toTheLeft = NORTH;
        }

        private NSEW toTheLeft;

        NSEW(String value) {
            this.value = value;
        }

        private NSEW toTheRight;

        public static NSEW getFromValue(String value) {
            return Arrays.stream(NSEW.values())
                    .filter(nsew -> nsew.value.equals(value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("This NSEW does not exist"));
        }

        public NSEW getToTheLeft() {
            return toTheLeft;
        }

        public NSEW getToTheRight() {
            return toTheRight;
        }

        public String getValue() {
            return value;
        }
    }

    public enum Obstacle {
        GROUND("GROUND"),
        BORDER("OUT_OF_RANGE");

        private String value;

        Obstacle(String value) {
            this.value = value;
        }

        public static Obstacle getFromValue(String value) {
            return Arrays.stream(Obstacle.values())
                    .filter(obs -> obs.value.equals(value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("This Obstacle does not exist"));
        }

        public String getValue() {
            return value;
        }
    }

    public enum Action {
        Scan,
        Fly,
        Echo,
        Heading,
        Stop
    }

    public enum SubState {
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
        CHANGELINE_DONE,

        PASS_ISLAND_STEP_1,
        PASS_ISLAND_STEP_2,

        ABOUT_TURN_L_1,
        ABOUT_TURN_L_2,
        ABOUT_TURN_L_3,
        ABOUT_TURN_L_4,
        ABOUT_TURN_L_5,
        ABOUT_TURN_L_6,
        ABOUT_TURN_L_7,
        ABOUT_TURN_R_1,
        ABOUT_TURN_R_2,
        ABOUT_TURN_R_3,
        ABOUT_TURN_R_4,
        ABOUT_TURN_R_5,
        ABOUT_TURN_R_6,
        ABOUT_TURN_R_7,
    }
}
