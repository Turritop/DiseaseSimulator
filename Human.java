import java.awt.*;

public class Human {

    public String disease;
    public boolean alive;
    public boolean infected;
    public boolean immune;
    public int roundsInfected;

    public Human(String disease) {
        this.disease = disease;
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

    private void kill() {
        alive = false;
    }

    private void cure() {
        infected = false;
        immune = true;
    }

    public boolean attemptToCure() {
        if (!infected) {
            return false;
        }
        //the chance of getting over an infection is an exponential random variable with given mean
        //F(x) = 1 - e^(-x/mean)
        double mean = Main.diseaseMap.get(disease)[0];
        double chanceToCure = 1 - Math.pow(Math.E, -1 * roundsInfected / mean); //will always be between 1 and 0
        if (Math.random() < chanceToCure) {
            cure();
            return true;
        }
        return false;
    }

    public boolean attemptToKill() {
        if (!infected) {
            return false;
        }
        //the chance of dying from an infection is an exponential random variable with given mean
        //F(x) = 1 - e^(-x/mean)
        double mean = Main.diseaseMap.get(disease)[1];
        double chanceToKill = 1 - Math.pow(Math.E, -1 * roundsInfected / mean); //will always be between 1 and 0
        if (Math.random() < chanceToKill) {
            kill();
            return true;
        }
        return false;
    }

    public boolean attemptToInfect() {
        if (!infected) {
            return false;
        }
        //each disease has a static chance to infect a neighboring person
        double chanceToInfect = Main.diseaseMap.get(disease)[3];
        if (Math.random() < chanceToInfect) {
            infect();
            return true;
        }
        return false;
    }

}
