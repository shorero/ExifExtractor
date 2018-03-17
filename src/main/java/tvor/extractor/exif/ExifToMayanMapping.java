/**
 *
 */
package tvor.extractor.exif;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author shore
 *
 */
public enum ExifToMayanMapping {
	//
	APERTURE("Aperture Value", "EXIF Aperture"),
	//
	BRIGHTNESS("Brightness Value", "EXIF Brightness"),
	//
	COLOR_SPACE("Color Space", "EXIF Color Space"),
	//
	COMPONENT_1("Component 1", "SKIP"),
	//
	COMPONENT_2("Component 2", "SKIP"),
	//
	COMPONENT_3("Component 3", "SKIP"),
	//
	COMPONENTS_CONFIGURATION("Components Configuration", "EXIF Components Configuration"),
	//
	COMPRESSION("Compression", "SKIP"),
	//
	COMPRESSION_TYPE("Compression Type", "EXIF Compression Type"),
	//
	CUSTOM_RENDERED("Custom Rendered", "EXIF Custom Rendered"),
	//
	DATA_PRECISION("Data Precision", "EXIF Data Precision"),
	//
	DATE_TIME("Date/Time", "EXIF Date Time"),
	//
	DATE_TIME_DIGITIZED("Date/Time Digitized", "EXIF Date Time Digitized"),
	//
	DATE_TIME_ORIGINAL("Date/Time Original", "EXIF Date Time Original"),
	//
	DIGITAL_ZOOM("Digital Zoom Ratio", "EXIF Digital Zoom"),
	//
	EXIF_IMAGE_HEIGHT("Exif Image Height", "EXIF Image Height"),
	//
	EXIF_IMAGE_WIDTH("Exif Image Width", "EXIF Image Width"),
	//
	EXIF_VERSION("Exif Version", "SKIP"),
	//
	EXPOSURE_BIAS("Exposure Bias Value", "EXIF Exposure Bias"),
	//
	EXPOSURE_MODE("Exposure Mode", "EXIF Exposure Mode"),
	//
	EXPOSURE_PROGRAM("Exposure Program", "EXIF Exposure Program"),
	//
	EXPOSURE_TIME("Exposure Time", "EXIF Exposure Time"),
	//
	F_NUMBER("F-Number", "EXIF F Number"),
	//
	FLASH("Flash", "EXIF Flash"),
	//
	FLASHPIX_VERSION("FlashPix Version", "EXIF FlashPix Version"),
	//
	FOCAL_LENGTH("Focal Length", "EXIF Focal Length"),
	//
	FOCAL_LENGTH_35("Focal Length 35", "EXIF Focal Length 35"),
	//
	GPS_ALTITUDE("GPS Altitude", "EXIF GPS Altitude"),
	//
	GPS_ALTITUDE_REF("GPS Altitude Ref", "EXIF GPS Altitude Ref"),
	//
	GPS_DATESTAMP("GPS Date Stamp", "EXIF GPS Date Stamp"),
	//
	GPS_LATITUDE("GPS Latitude", "EXIF GPS Latitude"),
	//
	GPS_LATITUDE_REF("GPS Latitude Ref", "EXIF GPS Latitude Ref"),
	//
	GPS_LONGITUDE("GPS Longitude", "EXIF GPS Longitude"),
	//
	GPS_LONGITUDE_REF("GPS Longitude Ref", "EXIF GPS Longitude Ref"),
	//
	GPS_SPEED("GPS Speed", "EXIF GPS Speed"),
	//
	GPS_SPEED_REF("GPS Speed Ref", "EXIF GPS Speed Ref"),
	//
	GPS_TIMESTAMP("GPS Time-Stamp", "EXIF GPS Timestamp"),
	//
	IMAGE_HEIGHT("Image Height", "EXIF Height"),
	//
	IMAGE_WIDTH("Image Width", "EXIF Width"),
	//
	ISO_SPEED_RATINGS("ISO Speed Ratings", "EXIF ISO Speed Ratings"),
	//
	LENS_MAKE("Lens Make", "EXIF Lens Make"),
	//
	LENS_MODEL("Lens Model", "EXIF Lens Model"),
	//
	LENS_SPECIFICATION("Lens Specification", "EXIF Lens Specification"),
	//
	MAKE("Make", "EXIF Make"),
	//
	METERING_MODE("Metering Mode", "EXIF Metering Mode"),
	//
	MODEL("Model", "EXIF Model"),
	//
	NUMBER_OF_COMPONENTS("Number of Components", "SKIP"),
	//
	ORIENTATION("Orientation", "EXIF Orientation"),
	//
	PADDING("Padding", "SKIP"),
	//
	RESOLUTION_UNIT("Resolution Unit", "EXIF Resolution Unit"),
	//
	RESOLUTION_UNITS("Resolution Units", "EXIF Resolution Units"),
	//
	SCENE_CAPTURE_TYPE("Scene Capture Type", "EXIF Scene Capture Type"),
	//
	SCENE_TYPE("Scene Type", "EXIF Scene Type"),
	//
	SENSING_METHOD("Sensing Method", "EXIF Sensing Method"),
	//
	SHUTTER_SPEED("Shutter Speed Value", "EXIF Shutter Speed"),
	//
	SOFTWARE("Software", "EXIF Software"),
	//
	SUBJECT_LOCATION("Subject Location", "EXIF Subject Location"),
	//
	SUBSEC_TIME_DIGITIZED("Sub-Sec Time Digitized", "EXIF Sub-Sec Time Digitized"),
	//
	SUBSEC_TIME_ORIGINAL("Sub-Sec Time Original", "EXIF Sub-Sec Time Original"),
	//
	THUMBNAIL_HEIGHT("Thumbnail Height Pixels", "SKIP"),
	//
	THUMBNAIL_WIDTH("Thumbnail Width Pixels", "SKIP"),
	//
	VERSION("Version", "SKIP"),
	//
	WHITE_BALANCE_MODE("White Balance Mode", "EXIF White Balance Mode"),
	//
	X_RESOLUTION("X Resolution", "EXIF X Resolution"),
	//
	Y_RESOLUTION("Y Resolution", "EXIF Y Resolution"),
	//
	YCBCR_POSITIONING("YCbCr Positioning", "EXIF YCbCr Positioning");

	public static Map<SortKey, ExifToMayanMapping> byExifName() {
		final Map<SortKey, ExifToMayanMapping> rtn = new HashMap<>();
		for (final ExifToMayanMapping m : ExifToMayanMapping.values()) {
			final SortKey k = new SortKey(m.getExifName(), 0);
			if (rtn.containsKey(k)) {
				throw new RuntimeException("Duplicate EXIF name: " + m.getExifName());
			}
			rtn.put(k, m);
		}
		return rtn;
	}

	public static Map<SortKey, ExifToMayanMapping> byMayanLabel() {
		final Map<SortKey, ExifToMayanMapping> rtn = new HashMap<>();
		for (final ExifToMayanMapping m : ExifToMayanMapping.values()) {
			if (m.getMayanLabel().equalsIgnoreCase("skip")) {
				continue;
			}
			final SortKey k = new SortKey(m.getMayanLabel(), 0);
			if (rtn.containsKey(k)) {
				throw new RuntimeException("Duplicate metadata name: " + m.getMayanLabel());
			}
			rtn.put(k, m);
		}
		return rtn;
	}

	public static Set<String> getExifSkipSet() {
		final Set<String> rtn = new HashSet<>();
		for (final ExifToMayanMapping m : ExifToMayanMapping.values()) {
			if (m.getMayanLabel().equalsIgnoreCase("skip")) {
				rtn.add(m.getExifName());
			}
		}
		return rtn;
	}

	private String exifName;
	private String mayanLabel;
	private String mayanName;

	ExifToMayanMapping(final String exifName, final String mayanLabel) {
		this.exifName = exifName;
		this.mayanLabel = mayanLabel;

		final String temp = mayanLabel.replaceAll("[ ]", "");
		if (temp.startsWith("EXIF")) {
			mayanName = "exif" + temp.substring("EXIF".length());
		} else {
			final char first = Character.toLowerCase(temp.charAt(0));
			mayanName = String.valueOf(first) + temp.substring(1);
		}
	}

	public String getExifName() {
		return exifName;
	}

	public String getMayanLabel() {
		return mayanLabel;
	}

	public String getMayanName() {
		return mayanName;
	}

}
