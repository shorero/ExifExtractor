/**
 *
 */
package tvor.extractor.exif.data;

import javax.ws.rs.core.Form;

/**
 * @author shore
 *
 */
public abstract class PostableData {
    public abstract Form toForm();
}
