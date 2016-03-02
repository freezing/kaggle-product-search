package com.kaggle.nlp;

import com.kaggle.service.LanguageModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BestCandidateFinder {
    private LanguageModelService languageModelService;

    public BestCandidateFinder(LanguageModelService languageModelService) {
        this.languageModelService = languageModelService;
    }

    public List<String> findBest(List<List<String>> candidates) {
        List<Double> d0 = init(candidates.get(0));
        int from[][] = new int[candidates.size()][100];
        for (int i = 0; i < from.length; i++)
            for (int j = 0; j < 100; j++) from[i][j] = -1;

        // Calculate probabilities and paths
        for (int i = 1; i < candidates.size(); i++) {
            List<Double> d1 = new ArrayList<>();
            List<String> prev = candidates.get(i - 1);
            List<String> curr = candidates.get(i);

            for (int j = 0; j < curr.size(); j++) {
                String s = curr.get(j);
                Double best = Double.NEGATIVE_INFINITY;
                for (int k = 0; k < prev.size(); k++) {
                    String w = prev.get(k);
                    Double t1 = d0.get(k);
                    Double t2 = languageModelService.logProbability(w, s);
                    Double logProbability = t1 + t2;

                    if (logProbability > best) {
                        best = logProbability;
                        // Coming from position k
                        from[i][j] = k;
                    }
                }
                d1.add(best);
            }
            d0 = d1;
        }

        // Find argmax_w d[n][w]
        int idx = -1;
        Double best = Double.NEGATIVE_INFINITY;
        List<String> lastCandidates = candidates.get(candidates.size() - 1);
        for (int j = 0; j < lastCandidates.size(); j++) {
            if (best < d0.get(j)) {
                best = d0.get(j);
                idx = j;
            }
        }

        // Create path starting from w
        List<String> ret = new ArrayList<>();
        int i = candidates.size() - 1;
        while (true) {
            ret.add(candidates.get(i).get(idx));
            idx = from[i][idx];
            i--;
            if (idx == -1) break;
        }

        // Reverse result
        List<String> actualOrder = new ArrayList<>();
        for (i = ret.size() - 1; i >= 0; i--) actualOrder.add(ret.get(i));
        return actualOrder;
    }

    private List<Double> init(List<String> a) {
        List<Double> d = new ArrayList<Double>();
        for (int i = 0; i < a.size(); i++) {
            d.add(languageModelService.logProbability(a.get(i)));
        }
        return d;
    }
}
