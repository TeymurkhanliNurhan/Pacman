
class Rectangle {
    private int x;
    private int y;
    private int width;
    private int height;
    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public boolean intersects(Rectangle r) {
        return this.x < r.getX() + r.getWidth() &&
                this.x + this.width > r.getX() &&
                this.y < r.getY() + r.getHeight() &&
                this.y + this.height > r.getY();
    }
}
