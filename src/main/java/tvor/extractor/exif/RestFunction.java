/**
 *
 */
package tvor.extractor.exif;

import java.text.MessageFormat;

/**
 * @author shore
 *
 */
public enum RestFunction {
    //
    LIST_ALLOWED_MAYAN_METADATA("api/metadata/document_types/{0}/metadata_types/"),
    //
    LIST_CURRENT_MAYAN_METADATA("api/metadata/documents/{0}/metadata/"),
    //
    LIST_MAYAN_DOCUMENT_TYPES("api/documents/document_types/"),
    //
    LIST_MAYAN_DOCUMENTS_FOR_TAG("api/tags/tags/{0}/documents/"),
    //
    LIST_MAYAN_METADATA_TYPES("api/metadata/metadata_types/"),
    //
    LIST_MAYAN_TAGS("api/tags/tags/"),
    //
    NEW_MAYAN_METADATA_TYPE("api/metadata/metadata_types/"),
    //
    NEW_MAYAN_METADATA_VALUE("api/metadata/documents/{0}/metadata/"),
    //
    REMOVE_MAYAN_TAG("api/tags/documents/{0}/tags/{1}/");

    private String function;

    RestFunction(final String function) {
        this.function = function;
    }

    public String getFunction(final Object... parameter) {
        if (parameter.length <= 0) {
            return function;
        }
        return MessageFormat.format(function, parameter);
    }
}
