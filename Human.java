import java.awt.*;

public class Human {

    private String disease;
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

    //returns the color to corresponds to this human's health
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
        infected = false;
    }

    private void cure() {
        infected = false;
        immune = true;
    }

    //attempt to cure this human with success depending on the disease
    //the chance of getting over an infection is an exponential random variable with given mean
    //F(x) = 1 - e^(-x/mean) where x is the number of rounds/hours this human has been sick
    public boolean attemptToCure() {
        if (!infected) {
            return false;
        }
        double meanDays = Main.diseaseMap.get(disease)[0];
        double chanceToCure = 1 - Math.pow(Math.E, -1 * roundsInfected / (meanDays / 24)); //will always be between 1 and 0

        if (Math.random() < chanceToCure) {
            cure();
            return true;
        }
        return false;
    }

    //attempt to kill this human with success depending on the disease
    //the chance of dying from an infection is an exponential random variable with given mean
    //F(x) = 1 - e^(-x/mean) where x is the number of rounds/hours this human has been sick
    public boolean attemptToKill() {
        if (!infected || immune || !alive) {
            return false;
        }
        double meanDays = Main.diseaseMap.get(disease)[1];
        double chanceToKill = 1 - Math.pow(Math.E, -1 * roundsInfected / (meanDays / 24)); //will always be between 1 and 0

        if (Math.random() < chanceToKill) {
            kill();
            return true;
        }
        return false;
    }

    //attempt to infect this human with success depending on the disease
    //each disease has a static chance to infect a neighboring person
    public boolean attemptToInfect() {
        if (immune) {
            return false;
        }
        double chanceToInfect = Main.diseaseMap.get(disease)[2];
        if (Math.random() < chanceToInfect) {
            infect();
            return true;
        }
        return false;
    }

}
