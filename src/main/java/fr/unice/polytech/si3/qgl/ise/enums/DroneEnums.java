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

        private String value;
        private NSEW toTheLeft;
        private NSEW toTheRight;

        NSEW(String value) {
            this.value = value;
        }

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

        public NSEW getToThe(ZQSD dir) {
            switch (dir) {
                case LEFT:
                    return toTheLeft;
                case RIGHT:
                    return toTheRight;
                case FRONT:
                    return this;
                case BACK:
                    return toTheLeft.toTheLeft;
            }

            throw new IllegalArgumentException("Direction does not exist");
        }

        public String getValue() {
            return value;
        }
    }

    public enum Obstacle {
        GROUND("GROUND"),
        BORDER("OUT_OF_RANGE");

        private final String value;

        Obstacle(String value) {
            this.value = value;
        }

        public static Obstacle getFromValue(String value) {
            return Arrays.stream(Obstacle.values())
                    .filter(obs -> obs.value.equals(value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("This Obstacle does not exist"));
        }

    }

    public enum Action {
        SCAN,
        FLY,
        ECHO,
        HEADING,
        STOP
    }
}
