import java.awt.*;

public class Human {

    public boolean alive;
    public boolean infected;
    public boolean immune;
    public int roundsInfected;

    public Human() {
        infected = false;
        immune = false;
        alive = true;
        roundsInfected = 0;
    }

    public Color getColor() {
        if (!alive) {
            return Color.black;
        } else if (infected) {
            return Color.red;
        } else if (immune) {
            return Color.white;
        } else {
            return Color.green;
        }
    }

    public void infect() {
        if (!immune && alive) {
            infected = true;
        }
    }

    public void kill() {
        alive = false;
    }

    public void cure() {
        infected = false;
        immune = true;
    }

}
