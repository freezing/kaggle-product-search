package com.kaggle.nlp;

public class JavaNlpUtils {
    public static int lcsMatch(String s, String w) {
        int d[][] = new int[s.length() + 1][w.length() + 1];
        int best = 0;
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 1; j <= w.length(); j++) {
                if (s.charAt(i - 1) == w.charAt(j - 1)) d[i][j] = d[i - 1][j - 1] + 1;
                else d[i][j] = 0;
                best = Math.max(best, d[i][j]);
            }
        }
        return best;
    }
}
