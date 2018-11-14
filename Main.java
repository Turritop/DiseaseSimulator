import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final int DRAWING_PANEL_WIDTH = 500;
    private static final int DRAWING_PANEL_HEIGHT = 250;
    private static final int NUM_HUMANS_WIDTH = 30;
    private static final int NUM_HUMANS_HEIGHT = 15;

    public static Map<String, double[]> diseaseMap;
    //the array goes: meanTimeUntilCure, meanTimeUntilDeath, infectionRate
    public static String diseaseChoice;
    private static double waitTimer = 100;

    public static void main(String[] args) {
        System.out.println("Welcome to the Disease Simulator\n");
        Scanner sc = new Scanner(System.in);
        diseaseMap = new HashMap<>();
        designDisease(sc);
        runSimulation(sc);
    }

    public static void designDisease(Scanner sc) {
        System.out.println("Do you want to design your own disease? Y/N");
        String next = sc.next();
        if (next.toLowerCase().contains("y")) {
            double[] parameters = new double[3];
            System.out.println();
            System.out.println("On average, how many days will someone be sick before they die from the disease?");
            double input = sc.nextDouble();
            parameters[1] = input * 24;
            System.out.println("On average, how many days will someone be sick before they get over the disease?");
            parameters[0] = sc.nextInt() * 24;
            System.out.println("How likely is it for someone to get infected by a neighboring sick person? (0-1)");
            input = sc.nextDouble();
            while (input > 1 || input < 0) {
                System.out.println("Invalid input: " + input);
                System.out.println("How likely is it for someone to get infected by a neighboring sick person? (0-1)");
                input = sc.nextDouble();
            }
            parameters[2] = input;
            diseaseMap.put("Custom", parameters);
            diseaseChoice = "Custom";
            System.out.println("\nCongratulations on designing your disease.");
        } else {
            implementDiseaseMap();
            System.out.println("Choose your disease \nType 1 for the flu, 2 for the bubonic plague, 3 for tuberculosis, 4 for ebola");
            int choice = sc.nextInt();
            while (choice <= 0 || choice > 4) {
                System.out.println("Invalid input: " + choice);
                System.out.println("Choose your disease \nType 1 for the flu, 2 for the bubonic plague, 3 for tuberculosis, 4 for ebola");
                choice = sc.nextInt();
            }
            if (choice == 1) {
                diseaseChoice = "Flu";
            } else if (choice == 2) {
                diseaseChoice = "Plague";
            } else if (choice == 3) {
                diseaseChoice = "Tuberculosis";
                waitTimer = 1;
            } else {
                diseaseChoice = "Ebola";
                waitTimer = 50;
            }
        }

    }

    public static void implementDiseaseMap() {
        String[] key1 = {"Flu", "Plague", "Tuberculosis", "Ebola"};
        //meanTimeUntilCure, meanTimeUntilDeath, infectionRate
        //sources: https://www.cdc.gov/flu/professionals/acip/2018-2019/background/background-epidemiology.htm
        double[] fluValues = {7 * 24, 14 * 24, .08};
        diseaseMap.put("Flu", fluValues);
        //sources: http://www.who.int/csr/resources/publications/plague/whocdscsredc992a.pdf  https://en.wikipedia.org/wiki/Bubonic_plague
        double[] plagueValues = {30, 24, .40};
        diseaseMap.put("Plague", plagueValues);
        //sources: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4235436/  https://www.ncbi.nlm.nih.gov/pmc/articles/PMC3070694/
        double[] tbValues = {7 * 24 * 52 * 13, 7 * 24 * 52 * 7, .1};
        diseaseMap.put("Tuberculosis", tbValues);
        //sources: http://www.who.int/news-room/fact-sheets/detail/ebola-virus-disease  https://en.wikipedia.org/wiki/Ebola_virus_disease
        double[] ebolaValues = {7 * 24 * 14, 7 * 24 * 12, .75};
        diseaseMap.put("Ebola", ebolaValues);
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
                        infectNeighbors(r, c, population);
                        currentHuman.attemptToCure();
                        if (currentHuman.attemptToKill()) {
                            killCounter++;
                        }
                    }

                }
            }

            double currentTime = System.currentTimeMillis();
            while (System.currentTimeMillis() < (currentTime + waitTimer)) {
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
                population[r][c] = new Human(diseaseChoice);
                if (r == patientZeroX && c == patientZeroY) {
                    population[r][c].infect();
                }
            }
        }
    }

    public static void infectNeighbors(int row, int col, Human[][] population) {
        for(int r = row - 1; r <= row + 1; r++) {
            for(int c = col - 1; c <= col + 1; c++) {
                if ((r != row || c != col)) {
                    Human h = population[r][c];
                    h.attemptToInfect();
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
