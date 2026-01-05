package utils;

import java.util.Random;

public class LicensePlateGenerator {

    private static final String LETTERS = "ABCDEFGHJKLMNPRSTUVWXYZ";
    // escluse I, O, Q per evitare ambiguità
    private static final Random random = new Random();

    public static String randomPlate() {
        return "" +
                randomLetter() +
                randomLetter() +
                randomNumber(3) +
                randomLetter() +
                randomLetter();
    }

    private static char randomLetter() {
        return LETTERS.charAt(random.nextInt(LETTERS.length()));
    }

    private static String randomNumber(int digits) {
        int max = (int) Math.pow(10, digits);
        return String.format("%0" + digits + "d", random.nextInt(max));
    }
}