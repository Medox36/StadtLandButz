package ch.giuntini.stadtlandbutz_host.stringsimilarity;

public class SimilarityScore {
    private final String key;
    private final double score;

    public SimilarityScore(String key, double score) {
        this.key = key;
        this.score = score;
    }

    public String getKey() {
        return this.key;
    }

    public double getScore() {
        return this.score;
    }

    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.key.hashCode();
        hash = 31 * hash + (int) (this.score * 1000000.0);
        return hash;
    }

    public boolean equals(Object o) {
        if (o != null && o.getClass() == this.getClass()) {
            SimilarityScore other = (SimilarityScore) o;
            return this.key.equals(other.key) && this.score == other.score;
        } else {
            return false;
        }
    }
}
