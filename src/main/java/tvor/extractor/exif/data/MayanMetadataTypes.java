/**
 *
 */
package tvor.extractor.exif.data;

import java.util.HashMap;
import java.util.Map;

import tvor.extractor.exif.ExifToMayanMapping;
import tvor.extractor.exif.SortKey;

/**
 * @author shore
 *
 */
public class MayanMetadataTypes {
	private int count;
	private String next;
	private String previous;
	private MayanMetadataType[] results;

	public Map<SortKey, MayanMetadataType> buildExifTypes() {
		final Map<SortKey, MayanMetadataType> rtn = new HashMap<>();
		final Map<SortKey, ExifToMayanMapping> byMayanLabel = ExifToMayanMapping.byMayanLabel();
		for (final MayanMetadataType mt : results) {
			// NOTE: the assumption here is that the EXIF mayan metadata types are unique.
			// Thus we can use '0' is the id value. However, this means that we can't detect
			// an error (using a dup EXIF name).
			final SortKey s = new SortKey(mt.getLabel(), 0);
			final ExifToMayanMapping e = byMayanLabel.get(s);
			if (e != null) {
				final SortKey k = new SortKey(e.getExifName(), 0);
				rtn.put(k, mt);
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
