/**
 *
 */
package tvor.extractor.exif.data;

import javax.ws.rs.core.Form;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author shore
 *
 */
public class NewMayanMetadataType extends PostableData {
    @JsonProperty("default")
    private String defaultValue = "";
    private String label;
    private String lookup = "";
    private String name;
    private String parser = "";
    private String validation = "";

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getLabel() {
        return label;
    }

    public String getLookup() {
        return lookup;
    }

    public String getName() {
        return name;
    }

    public String getParser() {
        return parser;
    }

    public String getValidation() {
        return validation;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public void setLookup(final String lookup) {
        this.lookup = lookup;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setParser(final String parser) {
        this.parser = parser;
    }

    public void setValidation(final String validation) {
        this.validation = validation;
    }

    @Override
    public Form toForm() {
        final Form rtn = new Form();
        rtn.param("default", defaultValue);
        rtn.param("label", label);
        rtn.param("lookup", lookup);
        rtn.param("name", name);
        rtn.param("parser", parser);
        rtn.param("validation", validation);
        return rtn;
    }

    @Override
    public String toString() {
        return "NewMetadataType [defaultValue=" + defaultValue + ", label=" + label + ", lookup=" + lookup + ", name="
                + name + ", parser=" + parser + ", validation=" + validation + "]";
    }
}
