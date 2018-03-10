/**
 *
 */
package tvor.extractor.exif.data;

/**
 * @author shore
 *
 */
public class MayanCurrentMetadata {
    private int count;
    private String next;
    private String previous;
    private MayanCurrentMetadataValue[] results;

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public MayanCurrentMetadataValue[] getResults() {
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

    public void setResults(final MayanCurrentMetadataValue[] results) {
        this.results = results;
    }
}
