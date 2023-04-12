package ch.giuntini.stadtlandbutz_host.stringsimilarity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StringSimilarityService {
    private final LevenshteinDistanceStrategy strategy;

    public StringSimilarityService(LevenshteinDistanceStrategy strategy) {
        this.strategy = strategy;
    }

    public List<SimilarityScore> scoreAll(List<String> features, String target) {
        ArrayList<SimilarityScore> scores = new ArrayList<>();

        for (String feature : features) {
            double score = this.strategy.score(feature, target);
            scores.add(new SimilarityScore(feature, score));
        }

        return scores;
    }

    public double score(String feature, String target) {
        return this.strategy.score(feature, target);
    }

    public SimilarityScore findTop(List<String> features, String target) {
        return this.findTop(features, target, new DescendingSimilarityScoreComparator());
    }

    public SimilarityScore findTop(List<String> features, String target, Comparator<SimilarityScore> comparator) {
        if (features.size() == 0) {
            return null;
        } else {
            List<SimilarityScore> scores = this.scoreAll(features, target);
            scores.sort(comparator);
            return scores.get(0);
        }
    }
}
