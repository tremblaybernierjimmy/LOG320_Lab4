package Laboratoire4;

public class Pion {
    public enum colors {
        none(0),
        black(2),
        white(4);

        private Integer value;
        private colors(Integer val) {
            value = val;
        }

        public Integer getValue() {
            return value;
        }
    }

    private int x;
    private int y;

    public Pion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}