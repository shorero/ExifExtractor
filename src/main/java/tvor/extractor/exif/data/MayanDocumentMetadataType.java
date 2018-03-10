package tvor.extractor.exif.data;

public class MayanDocumentMetadataType {
    private MayanDocumentType document_type;
    private int id;
    private MayanMetadataType metadata_type;
    private boolean required;
    private String url;

    public MayanDocumentType getDocument_type() {
        return document_type;
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

    public boolean isRequired() {
        return required;
    }

    public void setDocument_type(final MayanDocumentType document_type) {
        this.document_type = document_type;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setMetadata_type(final MayanMetadataType metadata_type) {
        this.metadata_type = metadata_type;
    }

    public void setRequired(final boolean required) {
        this.required = required;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
