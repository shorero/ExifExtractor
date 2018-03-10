/**
 *
 */
package tvor.extractor.exif.data;

import java.util.Arrays;

/**
 * @author shore
 *
 */
public class MayanDocumentType {
    private int delete_time_period;
    private String delete_time_unit;
    private String documents_count;
    private String documents_url;
    private String[] filenames;
    private int id;
    private String label;
    private int trash_time_period;
    private String trash_time_unit;
    private String url;

    public int getDelete_time_period() {
        return delete_time_period;
    }

    public String getDelete_time_unit() {
        return delete_time_unit;
    }

    public String getDocuments_count() {
        return documents_count;
    }

    public String getDocuments_url() {
        return documents_url;
    }

    public String[] getFilenames() {
        return filenames;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public int getTrash_time_period() {
        return trash_time_period;
    }

    public String getTrash_time_unit() {
        return trash_time_unit;
    }

    public String getUrl() {
        return url;
    }

    public void setDelete_time_period(final int delete_time_period) {
        this.delete_time_period = delete_time_period;
    }

    public void setDelete_time_unit(final String delete_time_unit) {
        this.delete_time_unit = delete_time_unit;
    }

    public void setDocuments_count(final String documents_count) {
        this.documents_count = documents_count;
    }

    public void setDocuments_url(final String documents_url) {
        this.documents_url = documents_url;
    }

    public void setFilenames(final String[] filenames) {
        this.filenames = filenames;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public void setTrash_time_period(final int trash_time_period) {
        this.trash_time_period = trash_time_period;
    }

    public void setTrash_time_unit(final String trash_time_unit) {
        this.trash_time_unit = trash_time_unit;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "DocumentType [delete_time_period=" + delete_time_period + ", delete_time_unit=" + delete_time_unit
                + ", documents_count=" + documents_count + ", documents_url=" + documents_url + ", filenames="
                + Arrays.toString(filenames) + ", id=" + id + ", label=" + label + ", trash_time_period="
                + trash_time_period + ", trash_time_unit=" + trash_time_unit + ", url=" + url + "]";
    }
}
