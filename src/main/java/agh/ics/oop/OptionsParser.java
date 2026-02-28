package agh.ics.oop;

import agh.ics.oop.model.MoveDirection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OptionsParser {
    public static List<MoveDirection> parse(String[] directions) {
        if (directions == null || directions.length == 0)
            return Collections.emptyList();

        List<MoveDirection> result = new ArrayList<>(directions.length);
        for (String dir : directions) {
            if (dir == null) continue;
            switch (dir) {
                case "f" -> result.add(MoveDirection.FORWARD);
                case "b" -> result.add(MoveDirection.BACKWARD);
                case "r" -> result.add(MoveDirection.RIGHT);
                case "l" -> result.add(MoveDirection.LEFT);
                default -> throw new IllegalArgumentException( dir + " is not legal move specification");
            }
        }       return result;
    }
}