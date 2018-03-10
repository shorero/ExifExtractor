/**
 *
 */
package tvor.extractor.exif.data;

/**
 * @author shore
 *
 */
public class MayanCurrentMetadataValue {
    private TaggedDocument document;
    private int id;
    private MayanMetadataType metadata_type;
    private String url;
    private String value;

    public TaggedDocument getDocument() {
        return document;
    }

    public int getId() {
        return id;
    }

    public MayanMetadataType getMetadata_type() {
        return metadata_type;
    }

    public String getUrl() {
        return url;
    }

    public String getValue() {
        return value;
    }

    public void setDocument(final TaggedDocument document) {
        this.document = document;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setMetadata_type(final MayanMetadataType metadata_type) {
        this.metadata_type = metadata_type;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setValue(final String value) {
        this.value = value;
    }

}
