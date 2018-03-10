/**
 *
 */
package tvor.extractor.exif.data;

import javax.ws.rs.core.Form;

import com.drew.metadata.Tag;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author shore
 *
 */
public class NewMayanMetadataValue extends PostableData {
    @JsonIgnore
    private Tag imageTag;
    private int metadata_type_pk;
    @JsonIgnore
    private MayanMetadataType metadataType;
    private String value;

    public Tag getImageTag() {
        return imageTag;
    }

    public int getMetadata_type_pk() {
        return metadata_type_pk;
    }

    public MayanMetadataType getMetadataType() {
        return metadataType;
    }

    public String getValue() {
        return value;
    }

    public void setImageTag(final com.drew.metadata.Tag imageTag) {
        this.imageTag = imageTag;
    }

    public void setMetadata_type_pk(final int metadata_type_pk) {
        this.metadata_type_pk = metadata_type_pk;
    }

    public void setMetadataType(final MayanMetadataType metadataType) {
        this.metadataType = metadataType;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public Form toForm() {
        final Form ret = new Form();
        ret.param("metadata_type_pk", String.valueOf(metadata_type_pk));
        ret.param("value", value);
        return ret;
    }

    @Override
    public String toString() {
        return "NewMayanMetadataValue [metadata_type_pk=" + metadata_type_pk + ", value=" + value + "]";
    }

}
