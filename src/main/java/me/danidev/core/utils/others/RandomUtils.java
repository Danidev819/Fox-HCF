package me.danidev.core.utils.others;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class RandomUtils {

    private static String[] alpha;
    private static String[] numeric;

    static {
        alpha = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        numeric = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
    }

    public static String randomAlphaNumeric(Integer maxChar) {
        ArrayList<String> alphaText = new ArrayList<String>();
        String[] alpha;
        for (int length = (alpha = RandomUtils.alpha).length, j = 0; j < length; ++j) {
            String text = alpha[j];
            alphaText.add(text);
        }
        String[] numeric;
        for (int length2 = (numeric = RandomUtils.numeric).length, k = 0; k < length2; ++k) {
            String nubmer = numeric[k];
            alphaText.add(nubmer);
        }
        StringBuilder randomName = null;
        for (int i = 0; i < maxChar; ++i) {
            Random random = new Random();
            int rand = random.nextInt(alphaText.size());
            if (randomName == null) {
                randomName = new StringBuilder(alphaText.get(rand));
            }
            randomName.append(alphaText.get(rand));
        }
        return Objects.requireNonNull(randomName).toString();
    }

    public static Integer randomNumeric(Integer maxChar) {
        ArrayList<String> alphaText = new ArrayList<>();
        String[] numeric;
        for (int length = (numeric = RandomUtils.numeric).length, j = 0; j < length; ++j) {
            String nubmer = numeric[j];
            alphaText.add(nubmer);
        }
        StringBuilder randomName = null;
        for (int i = 0; i < maxChar; ++i) {
            Random random = new Random();
            int rand = random.nextInt(alphaText.size());
            if (randomName == null) {
                randomName = new StringBuilder(alphaText.get(rand));
            }
            randomName.append(alphaText.get(rand));
        }
        return Integer.parseInt(Objects.requireNonNull(randomName).toString());
    }

    public static String randomString(Integer maxChar) {
        ArrayList<String> alphaText = new ArrayList<String>();
        String[] alpha;
        for (int length = (alpha = RandomUtils.alpha).length, j = 0; j < length; ++j) {
            String text = alpha[j];
            alphaText.add(text);
        }
        StringBuilder randomName = null;
        for (int i = 0; i < maxChar; ++i) {
            Random random = new Random();
            int rand = random.nextInt(alphaText.size());
            if (randomName == null) {
                randomName = new StringBuilder(alphaText.get(rand));
            }
            randomName.append(alphaText.get(rand));
        }
        return Objects.requireNonNull(randomName).toString();
    }
}
