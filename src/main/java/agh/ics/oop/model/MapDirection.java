package agh.ics.oop.model;

public enum MapDirection {
    NORTH(0,1,"Północ", "^"),
    NORTH_EAST(1, 1, "Północny Wschód", "↗"),
    EAST(1, 0, "Wschód", ">"),
    SOUTH_EAST(1, -1, "Południowy Wschód", "↘"),
    SOUTH(0, -1, "Południe", "∨"),
    SOUTH_WEST(-1, -1, "Południowy Zachód", "↙"),
    WEST(-1, 0, "Zachód", "<"),
    NORTH_WEST(-1, 1, "Północny Zachód", "↖");

    private final Vector2d unitVector;
    private final String directionStr;
    private final String shortcut;

    MapDirection(int x, int y, String directionStr, String shortcut) {
        this.unitVector = new Vector2d(x,y);
        this.directionStr = directionStr;
        this.shortcut = shortcut;
    }

    @Override
    public String toString(){
        return directionStr;
    }

    public String getShortcut(){
        return this.shortcut;
    }

    public MapDirection next(){
        MapDirection[] arr = values();
        return arr[(ordinal() +1) % arr.length];
    }
    public MapDirection previous() {
        MapDirection[] arr = values();
        return arr[(ordinal() -1 + arr.length) % arr.length];
    }

    public Vector2d toUnitVector() {
        return unitVector;
    }
}
