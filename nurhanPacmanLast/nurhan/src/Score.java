import java.io.Serializable;
public class Score implements Serializable {
    private String name;
    private int score;
    private String mapSize;
    public Score(String name, int score,String mapSize) {
        this.name = name;
        this.score = score;
        this.mapSize=mapSize;
    }
    public int getScore() {
        return score;
    }
    @Override
    public String toString() {
        return name + ": " + score+" -Map: "+mapSize;
    }
}


