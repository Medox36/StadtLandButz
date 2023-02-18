package ch.giuntini.stadtlandbutz_host.stringsimilarity;

import java.util.Comparator;

public class DescendingSimilarityScoreComparator implements Comparator<SimilarityScore> {

    public int compare(SimilarityScore x, SimilarityScore y) {
        double first = x.getScore();
        double second = y.getScore();
        if (first == second) {
            return 0;
        } else {
            return first < second ? 1 : -1;
        }
    }
}
