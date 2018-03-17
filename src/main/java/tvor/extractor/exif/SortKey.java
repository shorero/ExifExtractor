package tvor.extractor.exif;

public class SortKey implements Comparable<SortKey> {
	private final Integer id;
	private final String label;

	public SortKey(final String label, final Integer id) {
		this.id = id;
		this.label = label;
	}

	@Override
	public int compareTo(final SortKey o) {
		final int x = label.compareTo(o.label);
		if (x != 0) {
			return x;
		}
		return id.compareTo(o.id);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SortKey other = (SortKey) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		return true;
	}

	public Integer getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (id == null ? 0 : id.hashCode());
		result = prime * result + (label == null ? 0 : label.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "SortKey [id=" + id + ", label=" + label + "]";
	}

}
