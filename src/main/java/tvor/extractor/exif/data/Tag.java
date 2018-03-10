package tvor.extractor.exif.data;

public class Tag {
    private String color;
    private int documents_count;
    private String documents_url;
    private int id;
    private String label;
    private String url;

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
