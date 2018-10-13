import java.awt.*;
import java.util.Scanner;

public class Main {

    public static final int DRAWING_PANEL_WIDTH = 500;
    public static final int DRAWING_PANEL_HEIGHT = 250;
    public static final int NUM_HUMANS_WIDTH = 30;
    public static final int NUM_HUMANS_HEIGHT = 15;
    public static final double WAIT_TIMER = 100;

    public static int roundsBeforeDiseaseCanKill;
    public static int roundsBeforeDiseaseCure = 3;
    public static double infectionRate;
    public static double killRate;
    public static double cureRate;


    public static void main(String[] args) {
        System.out.println("Welcome to the Disease Simulator\n");
        Scanner sc = new Scanner(System.in);
        designDisease(sc);
        runSimulation(sc);
    }

    public static void designDisease(Scanner sc) {
        System.out.println("Design your disease:");
        System.out.println("What is the chance of infection (0-1)?");
        double input = sc.nextDouble();
        if (input == 42) {
            roundsBeforeDiseaseCanKill = 2 * 24;
            infectionRate = .5;
            killRate = .2;
            cureRate = .1;
        } else {
            infectionRate = input;
            System.out.println("How many hours must someone be sick before they can die from the disease?");
            roundsBeforeDiseaseCanKill = sc.nextInt();
            System.out.println("What is the chance of someone dying once they've reached that point (0-1)?");
            killRate = sc.nextDouble();
            System.out.println("What is the chance of a sick person getting better within the next hour (0-1)?");
            cureRate = sc.nextDouble();
            System.out.println("\nCongratulations on designing your disease.");
        }
    }

    public static void runSimulation(Scanner sc) {
        Human[][] population = new Human[NUM_HUMANS_WIDTH + 2][NUM_HUMANS_HEIGHT + 2]; //have a buffer around the actual data to avoid null-pointer exceptions
        populate(population);

        DrawingPanel dp = new DrawingPanel(DRAWING_PANEL_WIDTH, DRAWING_PANEL_HEIGHT);
        Graphics g = dp.getGraphics();

        boolean keepGoing = true;
        int roundCounter = 0; //each round represents one hour
        int killCounter = 0;
        while (keepGoing) {
            drawHumans(population, g);

            keepGoing = false;
            for (int r = 1; r <= NUM_HUMANS_WIDTH; r++) {
                for (int c = 1; c <= NUM_HUMANS_HEIGHT; c++) {
                    Human currentHuman = population[r][c]; //don't call on humans in buffer
                    if (currentHuman.infected) { //only keep going when at least one person is infected
                        keepGoing = true;
                        currentHuman.roundsInfected++; // need to keep track of this
                        infectNeighbors(r, c, population, infectionRate);
                        if (Math.random() < cureRate && currentHuman.roundsInfected >= roundsBeforeDiseaseCure) {
                            currentHuman.cure();
                        }
                        else if (currentHuman.roundsInfected >= roundsBeforeDiseaseCanKill && Math.random() < killRate && currentHuman.alive) {
                            currentHuman.kill();
                            killCounter++;
                        }
                    }

                }
            }

            double currentTime = System.currentTimeMillis();
            while (System.currentTimeMillis() < (currentTime + WAIT_TIMER)) {
            }
            roundCounter++;
        }

        System.out.println("It took " + roundCounter + " hours for your disease to run itself out.\nYour disease killed " + killCounter + " poeple.\nTry again? Y / N");
        if (sc.next().toLowerCase().equals("y")) {
            System.out.println("Would you like to use the same disease? Y/N");
            if (sc.next().toLowerCase().equals("y")) {
                runSimulation(sc);
            } else {
                designDisease(sc);
                runSimulation(sc);
            }
        }
    }

    public static void populate(Human[][] population) {
        int patientZeroY = (int) (Math.random() * NUM_HUMANS_WIDTH) + 1;
        int patientZeroX = (int) (Math.random() * NUM_HUMANS_HEIGHT) + 1;
        for (int r = 0; r < population.length; r++) {
            for (int c = 0; c < population[0].length; c++) {
                population[r][c] = new Human();
                if (r == patientZeroX && c == patientZeroY) {
                    population[r][c].infect();
                }
            }
        }
    }

    public static void infectNeighbors(int row, int col, Human[][] population, double infectionRate) {
        for(int r = row - 1; r <= row + 1; r++) {
            for(int c = col - 1; c <= col + 1; c++) {
                if ((r != row || c != col) //can't re-infect the original human
                        && Math.random() < infectionRate) {
                    Human h = population[r][c];
                    h.infect();
                }
            }
        }
    }

    public static void drawHumans(Human[][] population, Graphics g) {
        int humanWidth = DRAWING_PANEL_WIDTH / NUM_HUMANS_WIDTH;
        int humanHeight = DRAWING_PANEL_HEIGHT / NUM_HUMANS_HEIGHT;
        int x = 0;
        int y;
        for(int r = 1; r < population.length - 1; r++) { //don't draw humans in buffer
            y = 0;
            for(int c = 1; c < population[0].length - 1; c++) {
                //draw human
                g.setColor(population[r][c].getColor());
                g.fillOval(x, y, humanWidth, humanHeight);
                g.setColor(Color.black);
                g.drawOval(x, y, humanWidth, humanHeight);
                y += humanHeight;
            }
            x += humanWidth;
        }
    }
}
