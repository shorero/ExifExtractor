package tvor.extractor.exif.data;

public class Tag {
	private String color;
	private int documents_count;
	private String documents_url;
	private int id;
	private String label;
	private String url;

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
		final Tag other = (Tag) obj;
		if (id != other.id) {
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

	public String getColor() {
		return color;
	}

	public int getDocuments_count() {
		return documents_count;
	}

	public String getDocuments_url() {
		return documents_url;
	}

	public int getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + (label == null ? 0 : label.hashCode());
		return result;
	}

	public void setColor(final String color) {
		this.color = color;
	}

	public void setDocuments_count(final int documents_count) {
		this.documents_count = documents_count;
	}

	public void setDocuments_url(final String documents_url) {
		this.documents_url = documents_url;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Tag [color=" + color + ", documents_count=" + documents_count + ", documents_url=" + documents_url
				+ ", id=" + id + ", label=" + label + ", url=" + url + "]";
	}

}
