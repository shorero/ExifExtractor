package tvor.extractor.exif.data;

public class MayanDocumentAllowedMetadata {
    private int count;
    private String next;
    private String previous;
    private MayanDocumentMetadataType[] results;

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public MayanDocumentMetadataType[] getResults() {
        return results;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public void setNext(final String next) {
        this.next = next;
    }

    public void setPrevious(final String previous) {
        this.previous = previous;
    }

    public void setResults(final MayanDocumentMetadataType[] results) {
        this.results = results;
    }

}
