/**
 *
 */
package tvor.extractor.exif.data;

import java.util.HashMap;
import java.util.Map;

import tvor.extractor.exif.ExifToMayanMapping;

/**
 * @author shore
 *
 */
public class MayanMetadataTypes {
    private int count;
    private String next;
    private String previous;
    private MayanMetadataType[] results;

    public Map<String, MayanMetadataType> buildExifTypes() {
        final Map<String, MayanMetadataType> rtn = new HashMap<>();
        final Map<String, ExifToMayanMapping> byMayanLabel = ExifToMayanMapping.byMayanLabel();
        for (final MayanMetadataType mt : results) {
            final ExifToMayanMapping e = byMayanLabel.get(mt.getLabel());
            if (e != null) {
                rtn.put(e.getExifName(), mt);
            }
        }

        return rtn;
    }

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public MayanMetadataType[] getResults() {
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

    public void setResults(final MayanMetadataType[] results) {
        this.results = results;
    }

}
