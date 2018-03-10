/**
 *
 */
package tvor.extractor.exif;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import tvor.extractor.exif.data.MayanCurrentMetadata;
import tvor.extractor.exif.data.MayanCurrentMetadataValue;
import tvor.extractor.exif.data.MayanDocumentAllowedMetadata;
import tvor.extractor.exif.data.MayanDocumentMetadataType;
import tvor.extractor.exif.data.MayanMetadataType;
import tvor.extractor.exif.data.MayanMetadataTypes;
import tvor.extractor.exif.data.NewMayanMetadataType;
import tvor.extractor.exif.data.NewMayanMetadataValue;
import tvor.extractor.exif.data.PostableData;
import tvor.extractor.exif.data.Tag;
import tvor.extractor.exif.data.TaggedDocument;
import tvor.extractor.exif.data.TaggedDocuments;
import tvor.extractor.exif.data.TagsResult;
import tvor.extractor.exif.filter.ResponseFilter;

/**
 * @author shore
 *
 */
public class Main {

    private static final Set<String> SKIPPED_DICTIONARY = new HashSet<>();
    static {
        Main.SKIPPED_DICTIONARY.add("Apple Makernote");
        Main.SKIPPED_DICTIONARY.add("Huffman");
        Main.SKIPPED_DICTIONARY.add("File Type");
        Main.SKIPPED_DICTIONARY.add("XMP");
        Main.SKIPPED_DICTIONARY.add("Exif Thumbnail");
    }

    /**
     * @param arg
     *            the arguments passed into main() by the Java environment
     *
     * @throws IOException
     *             if there is an error extracting command-line data or processing a
     *             document
     * @throws ImageProcessingException
     *             if there is an error processing a document
     */
    public static void main(final String[] arg) throws IOException, ImageProcessingException {
        new Main().execute(arg);
    }

    /**
     * Add any metadata types declared in ExifToMayanMapping but that don't actually
     * exist, to the Mayan database
     *
     * @param argMap
     */
    private void addMissingMetadata(final Map<ArgKey, String> argMap) {
        // Get a list of the mappings, keyed by the Mayan label
        final Map<String, ExifToMayanMapping> mayanLabelMap = ExifToMayanMapping.byMayanLabel();

        // Remove from the map Mayan labels that correspond to existing metadata types
        String nextUrl = buildUrl(argMap, RestFunction.LIST_MAYAN_METADATA_TYPES.getFunction());
        do {
            final MayanMetadataTypes mdts = callApiGetter(MayanMetadataTypes.class, nextUrl, argMap);
            for (final MayanMetadataType m : mdts.getResults()) {
                mayanLabelMap.remove(m.getLabel());
            }
            nextUrl = mdts.getNext();
        } while (nextUrl != null);

        // Add any remaining labels to the Mayan database
        // Do the inserts in alphabetic order.
        final Map<String, ExifToMayanMapping> sortedMapping = new TreeMap<>();
        for (final ExifToMayanMapping m : mayanLabelMap.values()) {
            sortedMapping.put(m.getMayanLabel(), m);
        }
        for (final ExifToMayanMapping m : sortedMapping.values()) {
            final NewMayanMetadataType nmt = new NewMayanMetadataType();
            nmt.setLabel(m.getMayanLabel());
            nmt.setName(m.getMayanName());
            final Response r = callApiPoster(nmt, buildUrl(argMap, RestFunction.NEW_MAYAN_METADATA_TYPE.getFunction()),
                    argMap);
            if (!r.getStatusInfo().equals(Response.Status.CREATED)) {
                throw new RuntimeException("Did not create: " + nmt);
            }
        }
    }

    /**
     * Use the appropriate REST service to download the document itself
     *
     * @param doc
     *            the Mayan tagged document. This structure contains the download
     *            URL for the document itself.
     * @param argMap
     *            the map of command-line arguments
     * @return
     */
    private InputStream buildDocumentInputStream(final TaggedDocument doc, final Map<ArgKey, String> argMap) {
        final HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                .credentials(argMap.get(ArgKey.MAYAN_USERID), argMap.get(ArgKey.MAYAN_PASSWORD)).build();
        final ClientConfig config = new ClientConfig();
        config.register(feature);
        config.register(MultiPartFeature.class);
        // config.register(ResponseFilter.class);
        final Client client = ClientBuilder.newClient(config);

        client.property("accept", doc.getLatest_version().getMimetype());
        final WebTarget target = client.target(doc.getLatest_version().getDownload_url());
        final Invocation.Builder ib = target.request();
        final Response response = ib.get();

        final int responseCode = response.getStatus();
        if (responseCode != 200) {
            throw new RuntimeException(
                    "Attempt to get " + doc.getLatest_version().getDownload_url() + ": " + responseCode);
        }

        return response.readEntity(InputStream.class);
    }

    /**
     * Given the argument map and a (relative) URL path, build the full URL
     *
     * @param argMap
     *            the map of command-line arguments. Among other things, this map
     *            contains the base URL
     * @param function
     *            the path to be appended to the base URL
     * @return
     */
    private String buildUrl(final Map<ArgKey, String> argMap, final String function) {
        final String base = argMap.get(ArgKey.MAYAN_BASE_URL);
        if (base.endsWith("/")) {
            return base + function;
        }
        return base + "/" + function;
    }

    /**
     * Call a getter-type REST service
     *
     * @param theClass
     *            the data class into which the returned JSON gets parsed
     * @param targetUrl
     *            the (full) target URL for the service call
     * @param argMap
     *            the map of command-line parameters. Among other things, this
     *            contains the Mayan userid and password
     *
     * @return an instance of the class, containing the JSON data
     */
    private <T> T callApiGetter(final Class<T> theClass, final String targetUrl, final Map<ArgKey, String> argMap) {
        final WebTarget target = setUpRestCall(argMap, targetUrl);
        final Invocation.Builder ib = target.request(MediaType.APPLICATION_JSON);
        return ib.get(theClass);
    }

    /**
     * Call a post-type REST service
     *
     * @param theData
     *            the data to be posted
     * @param targetUrl
     *            the full URL for the service
     * @param argMap
     *            the map of command-line arguments. Among other things, this
     *            contains the Mayan userid and password
     *
     * @return the Response object from the request
     */
    private <T extends PostableData> Response callApiPoster(final T theData, final String targetUrl,
            final Map<ArgKey, String> argMap) {
        return callApiPoster(theData, targetUrl, argMap, false);
    }

    /**
     * Call a post-type REST service
     *
     * @param theData
     *            the data to be posted
     * @param targetUrl
     *            the full URL for the service
     * @param argMap
     *            the map of command-line arguments. Among other things, this
     *            contains the Mayan userid and password
     * @param registerResponseFilter
     *            'true' means to attach a logging filter to the post request
     *
     * @return the Response object from the request
     */
    private <T extends PostableData> Response callApiPoster(final T theData, final String targetUrl,
            final Map<ArgKey, String> argMap, final boolean registerResponseFilter) {
        final Entity<T> entity = Entity.entity(theData, MediaType.APPLICATION_JSON);
        final WebTarget target = setUpRestCall(argMap, targetUrl, registerResponseFilter);
        final Invocation.Builder ib = target.request(MediaType.APPLICATION_JSON);

        return ib.post(entity);
    }

    /**
     * Execute the processing functions of this class. This method is a separate
     * public method to allow this class to be used from other classes.
     *
     * @param arg
     *            the command-line argument array
     *
     * @throws IOException
     *             if there is a problem extracting command-line data or processing
     *             a document
     * @throws ImageProcessingException
     *             if there is an error processing a document
     */
    public void execute(final String[] arg) throws IOException, ImageProcessingException {
        final Map<ArgKey, String> argMap = extractCommandLineData(arg);

        if (argMap.get(ArgKey.ADD_MAYAN_METADATA).equals("true")) {
            addMissingMetadata(argMap);
        }

        final Map<String, MayanMetadataType> typeByExifName = new HashMap<>();
        String nextUrl = buildUrl(argMap, RestFunction.LIST_MAYAN_METADATA_TYPES.getFunction());
        do {
            final MayanMetadataTypes mdts = callApiGetter(MayanMetadataTypes.class, nextUrl, argMap);
            typeByExifName.putAll(mdts.buildExifTypes());
            nextUrl = mdts.getNext();
        } while (nextUrl != null);

        final Map<String, Tag> tagMap = getTagMap(argMap);
        final Tag unprocessed = tagMap.get(argMap.get(ArgKey.MAYAN_UNPROCESSED_LABEL));
        if (unprocessed == null) {
            throw new RuntimeException("No Mayan tag defined for label: " + argMap.get(ArgKey.MAYAN_UNPROCESSED_LABEL));
        }

        final Set<String> exifSkipSet = ExifToMayanMapping.getExifSkipSet();
        final Map<String, ExifToMayanMapping> enumByExifName = ExifToMayanMapping.byExifName();
        final Map<Integer, Set<String>> allowedMetadataMap = new HashMap<>();
        nextUrl = buildUrl(argMap, RestFunction.LIST_MAYAN_DOCUMENTS_FOR_TAG.getFunction(unprocessed.getId()));
        do {
            final TaggedDocuments docs = callApiGetter(TaggedDocuments.class, nextUrl, argMap);
            for (final TaggedDocument d : docs.getResults()) {
                processDocument(d, unprocessed, argMap, typeByExifName, enumByExifName, allowedMetadataMap,
                        exifSkipSet);
            }
            nextUrl = docs.getNext();
        } while (nextUrl != null);
    }

    /**
     * Extract the individual named fields from the command-line array
     *
     * @param arg
     *            the command line array
     *
     * @return a map indicating the values for each of the named fields
     *
     * @throws IOException
     *             if there is a problem prompting the user for argument data
     */
    private Map<ArgKey, String> extractCommandLineData(final String[] arg) throws IOException {
        if (arg.length <= 0) {
            return promptForArgs();
        }
        return processArgs(arg);
    }

    /**
     * Download the labels for all existing Mayan metadata types attached to a
     * document
     *
     * @param doc
     *            the document in question
     * @param argMap
     *            the map of command-line arguments
     *
     * @return a set of the Mayan metadata-type labels already associated with the
     *         document
     */
    private Set<String> extractExistingMetadataLabels(final TaggedDocument doc, final Map<ArgKey, String> argMap) {
        String nextUrl = buildUrl(argMap, RestFunction.LIST_CURRENT_MAYAN_METADATA.getFunction(doc.getId()));
        final Set<String> rtn = new HashSet<>();
        do {
            final MayanCurrentMetadata cmd = callApiGetter(MayanCurrentMetadata.class, nextUrl, argMap);
            for (final MayanCurrentMetadataValue v : cmd.getResults()) {
                rtn.add(v.getMetadata_type().getLabel());
            }
            nextUrl = cmd.getNext();
        } while (nextUrl != null);
        return rtn;
    }

    /**
     * Build a map of all existing Mayan tags
     *
     * @param argMap
     *            the map of command-line arguments
     *
     * @return a map of existing tags. The key of the map is the Mayan tag label.
     *         The value is the full data object for the tag.
     */
    private Map<String, Tag> getTagMap(final Map<ArgKey, String> argMap) {
        final Map<String, Tag> rtn = new HashMap<>();
        String nextUrl = buildUrl(argMap, RestFunction.LIST_MAYAN_TAGS.getFunction());
        do {
            final TagsResult r = callApiGetter(TagsResult.class, nextUrl, argMap);
            // System.out.println("Tags result: " + r);
            for (final Tag t : r.getResults()) {
                rtn.put(t.getLabel(), t);
            }
            nextUrl = r.getNext();
        } while (nextUrl != null);
        return rtn;
    }

    /**
     * Print usage information
     */
    private void printHelp() {
        final PrintStream p = System.out;
        p.println(
                "Usage: java Main -a -b <base URL> -h -i -l <unprocessed label> -p <Mayan password> -u <Mayan userid>");
        p.println("Switch interpretations (switches are case-insensitive; values are case-sensitive):");
        p.println("  -a: set the 'add metadata' flag to true; add metadata types as necessary.");
        p.println("  -b: set the base URL for REST service calls.");
        p.println("  -h: print this help.");
        p.println("  -i: set the 'ignore unrecognized' flag to true; ignore unexpected EXIF fields.");
        p.println("  -l: tag on unprocessed Mayan documents; this app processes all unprocessed documents");
        p.println("      and then removes this tag.");
        p.println("  -p: password for Mayan access.");
        p.println("  -u: userid for Mayan access.");
    }

    /**
     * Process the argument array into the argument map.
     *
     * @param arg
     *            the argument array passed to the main() method
     *
     * @return the argument map. The map key is one of the values in the ArgKey
     *         enum. The value is the value for the corresponding argument in the
     *         array. Note that all the values are required, but defaults are
     *         provided for the true/false values.
     */
    private Map<ArgKey, String> processArgs(final String[] arg) {
        final Map<ArgKey, String> rtn = new EnumMap<>(ArgKey.class);
        rtn.put(ArgKey.ADD_MAYAN_METADATA, "false");
        rtn.put(ArgKey.IGNORE_UNRECOGNIZED_EXIF, "false");
        for (int i = 0; i < arg.length; ++i) {
            switch (arg[i]) {
            case "-a":
            case "-A":
                rtn.put(ArgKey.ADD_MAYAN_METADATA, "true");
                break;
            case "-b":
            case "-B":
                ++i;
                final String a = arg[i].trim();
                if (arg[i].endsWith("/")) {
                    rtn.put(ArgKey.MAYAN_BASE_URL, a.substring(0, a.length() - 1));
                } else {
                    rtn.put(ArgKey.MAYAN_BASE_URL, a);
                }
            case "-h":
            case "-H":
                printHelp();
                System.exit(0);
                break;
            case "-i":
            case "-I":
                rtn.put(ArgKey.IGNORE_UNRECOGNIZED_EXIF, "true");
                break;
            case "-l":
            case "-L":
                rtn.put(ArgKey.MAYAN_UNPROCESSED_LABEL, arg[++i].trim());
                break;
            case "-p":
            case "-P":
                rtn.put(ArgKey.MAYAN_PASSWORD, arg[++i].trim());
                break;
            case "-u":
            case "-U":
                rtn.put(ArgKey.MAYAN_USERID, arg[++i].trim());
                break;
            default:
                System.out.println("Unrecognized command-line argument: " + arg[i]);
                printHelp();
                System.exit(1);
            }
        }
        if (rtn.get(ArgKey.MAYAN_BASE_URL) == null) {
            throw new RuntimeException("Base URL not specified");
        }
        if (rtn.get(ArgKey.MAYAN_UNPROCESSED_LABEL) == null) {
            throw new RuntimeException("Unprocessed tag label not specified");
        }
        if (rtn.get(ArgKey.MAYAN_PASSWORD) == null) {
            throw new RuntimeException("Mayan password not specified");
        }
        if (rtn.get(ArgKey.MAYAN_USERID) == null) {
            throw new RuntimeException("Mayan userid not specified");
        }
        return rtn;
    }

    /**
     * Process a single document
     *
     * @param doc
     *            the TaggedDocument instance to be processed
     * @param unprocessed
     *            the unprocessed-document tag, to be removed after processing is
     *            finished
     * @param argMap
     *            the map of command-line arguments
     * @param byExifName
     *            a map using the extracted EXIF name for the key. This is the name
     *            appearing in an extracted image metadata value. The corresponding
     *            Mayan metadata instance is the value
     * @param enumByExifName
     *            a map using the extracted EXIF name for the key. This is the name
     *            appearing in an extracted image metadata value. The mapping enum
     *            from this name to the Mayan metadata label is the value.
     * @param allowedMetadataMap
     *            a map using the integer PK for a Mayan document type as the key.
     *            The value is a set of the Mayan metadata labels (from the
     *            ExifToMayanMapping enum values) allowed for that document type.
     *            Note that this map must be populated with the allowed metadata for
     *            the indicated doc's Mayan document type before this method gets
     *            called. No allowed metadata for this doc's type causes a
     *            RuntimeException.
     * @param exifSkipSet
     *            a set of the EXIF field names to be skipped, per the
     *            ExifToMayanMapping enum
     *
     * @throws ImageProcessingException
     *             if there is an error processing an image
     * @throws IOException
     *             if there is an error processing an image
     */
    private void processDocument(final TaggedDocument doc, final Tag unprocessed, final Map<ArgKey, String> argMap,
            final Map<String, MayanMetadataType> byExifName, final Map<String, ExifToMayanMapping> enumByExifName,
            final Map<Integer, Set<String>> allowedMetadataMap, final Set<String> exifSkipSet)
            throws ImageProcessingException, IOException {
        if (doc.getLatest_version().getMimetype().startsWith("image")) {
            if (allowedMetadataMap.get(doc.getDocument_Type().getId()) == null) {
                updateAllowedMetadataMap(doc, allowedMetadataMap, argMap);
            }
            final Set<String> allowedMetadataSet = allowedMetadataMap.get(doc.getDocument_Type().getId());
            if (allowedMetadataSet == null) {
                throw new RuntimeException("Allowed metadata set not retrieved for " + doc.getDocument_Type().getId()
                        + "/" + doc.getDocument_Type().getLabel());
            }
            processImage(doc, argMap, byExifName, allowedMetadataSet, exifSkipSet);
        }
        removeTag(doc, unprocessed, argMap);
    }

    /**
     * Process the image indicated by the doc parameter. This method downloads the
     * image, runs it through an image metadata extraction library, and adds the
     * resulting values to the indicated document as metadata fields.
     *
     * @param doc
     *            a tagged document, indicating the image to process
     * @param argMap
     *            the map of command-line parameter values
     * @param byExifName
     *            a map that uses the EXIF field name as the key. This is the value
     *            that appears in the extracted image metadata. The value is the
     *            Mayan metadata type corresponding to that field name
     * @param allowedMetadataSet
     *            the set of string Mayan metadata labels, indicating the subset of
     *            the EXIF labels allowed for the Mayan type of the tagged document
     * @param exifSkipSet
     *            a set of the EXIF field names that are to be skipped
     *
     * @throws ImageProcessingException
     *             if there is a non-IO error reading or processing the current
     *             image
     * @throws IOException
     *             if there is an error in the image-reader for the current image
     */
    private void processImage(final TaggedDocument doc, final Map<ArgKey, String> argMap,
            final Map<String, MayanMetadataType> byExifName, final Set<String> allowedMetadataSet,
            final Set<String> exifSkipSet) throws ImageProcessingException, IOException {
        if (allowedMetadataSet == null) {
            throw new IllegalArgumentException("Null allowed-metadata set not allowed");
        }
        // Insert in label order, not extraction order
        final Map<String, NewMayanMetadataValue> insertionMap = new TreeMap<>();
        // also, don't insert metadata that already exist
        final Set<String> existingLabels = extractExistingMetadataLabels(doc, argMap);

        final InputStream in = buildDocumentInputStream(doc, argMap);
        final Metadata metadata = ImageMetadataReader.readMetadata(in);
        final String targetUrl = buildUrl(argMap, RestFunction.NEW_MAYAN_METADATA_VALUE.getFunction(doc.getId()));

        for (final Directory directory : metadata.getDirectories()) {
            if (Main.SKIPPED_DICTIONARY.contains(directory.getName())) {
                continue;
            }
            validateDirectory(directory);

            for (final com.drew.metadata.Tag tag : directory.getTags()) {
                if (tag.getTagName().startsWith("Unknown ")) {
                    continue;
                }
                if (exifSkipSet.contains(tag.getTagName())) {
                    continue;
                }
                final MayanMetadataType mdt = byExifName.get(tag.getTagName());
                if (mdt == null) {
                    if (argMap.get(ArgKey.IGNORE_UNRECOGNIZED_EXIF).equals("true")) {
                        continue;
                    } // else
                    throw new RuntimeException("No EXIF -> Mayan mapping for EXIF tag " + tag.getTagName() + " ("
                            + tag.getDescription() + ") in directory " + directory.getName());
                }
                if (existingLabels.contains(mdt.getLabel())) {
                    continue;
                }
                if (allowedMetadataSet.contains(mdt.getLabel())) {
                    final NewMayanMetadataValue v = new NewMayanMetadataValue();
                    v.setMetadata_type_pk(mdt.getId());
                    v.setValue(tag.getDescription());
                    v.setImageTag(tag);
                    v.setMetadataType(mdt);
                    insertionMap.put(mdt.getLabel(), v);
                }
            }
        }
        // Do the actual insertion, in map order not extraction order
        for (final NewMayanMetadataValue v : insertionMap.values()) {
            final Response r = callApiPoster(v, targetUrl, argMap, false);
            if (!r.getStatusInfo().equals(Response.Status.CREATED)) {
                throw new RuntimeException("Could not create " + v + "\n== (" + v.getImageTag().getTagName() + " -> "
                        + v.getMetadataType().getLabel() + "/" + v.getImageTag().getDescription() + ")\n==via URL "
                        + targetUrl + "\n==Metadata type: " + v.getMetadataType() + "\n==for document " + doc + ":\n=="
                        + r);
            }
        }
    }

    /**
     * If the command-line argument array is empty, then this class prompts for all
     * the information required to populate the argument map. This method is
     * necessary to allow the Main class to be run from inside the Eclipse IDE
     * without having to define a special run entry in Eclipse.
     *
     * @return a map of values as entered by the user.
     *
     * @throws IOException
     *             if there is a problem reading data from the terminal
     */
    private Map<ArgKey, String> promptForArgs() throws IOException {
        final Map<ArgKey, String> rtn = new EnumMap<>(ArgKey.class);
        rtn.put(ArgKey.MAYAN_BASE_URL, "http://mayan.tvor.support:29880");
        rtn.put(ArgKey.MAYAN_UNPROCESSED_LABEL, "Unprocessed");
        rtn.put(ArgKey.ADD_MAYAN_METADATA, "false");
        rtn.put(ArgKey.IGNORE_UNRECOGNIZED_EXIF, "false");

        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("    (-h on the command line prints help)");

        System.out.print("Mayan userid (-u): ");
        System.out.flush();
        String x = in.readLine();
        if (x == null) {
            throw new RuntimeException("Null userid");
        }
        if ((x = x.trim()).equals("")) {
            throw new RuntimeException("Empty userid");
        }
        rtn.put(ArgKey.MAYAN_USERID, x);

        System.out.print("Mayan password (-p): ");
        System.out.flush();
        x = in.readLine();
        if (x == null) {
            throw new RuntimeException("Null password");
        }
        if ((x = x.trim()).equals("")) {
            throw new RuntimeException("Empty password");
        }
        rtn.put(ArgKey.MAYAN_PASSWORD, x);

        System.out.print("Base URL (-b) (" + rtn.get(ArgKey.MAYAN_BASE_URL) + "): ");
        System.out.flush();
        x = in.readLine();
        if (x == null) {
            // do nothing - take the default
        } else if ((x = x.trim()).equals("")) {
            // do nothing - take the default
        } else {
            rtn.put(ArgKey.MAYAN_BASE_URL, x);
        }

        System.out.print("Unprocessed tag (-l) (" + rtn.get(ArgKey.MAYAN_UNPROCESSED_LABEL) + "): ");
        System.out.flush();
        x = in.readLine();
        if (x == null) {
            // do nothing - take the default
        } else if ((x = x.trim()).equals("")) {
            // do nothing - take the default
        } else {
            rtn.put(ArgKey.MAYAN_UNPROCESSED_LABEL, x);
        }

        System.out.print("Ignore unrecognized EXIF fields? (true/false) (-i) ("
                + rtn.get(ArgKey.IGNORE_UNRECOGNIZED_EXIF) + "): ");
        System.out.flush();
        x = in.readLine();
        if (x == null) {
            // do nothing - take the default
        } else if ((x = x.trim()).equals("")) {
            // do nothing - take the default
        } else {
            if (x.equalsIgnoreCase("true")) {
                rtn.put(ArgKey.IGNORE_UNRECOGNIZED_EXIF, "true");
            } else {
                rtn.put(ArgKey.IGNORE_UNRECOGNIZED_EXIF, "false");
            }
        }

        System.out.print("Create missing metadata? (true/false) (-a) (" + rtn.get(ArgKey.ADD_MAYAN_METADATA) + "): ");
        System.out.flush();
        x = in.readLine();
        if (x == null) {
            // do nothing - take the default
        } else if ((x = x.trim()).equals("")) {
            // do nothing - take the default
        } else {
            if (x.equalsIgnoreCase("true")) {
                rtn.put(ArgKey.ADD_MAYAN_METADATA, "true");
            } else {
                rtn.put(ArgKey.ADD_MAYAN_METADATA, "false");
            }
        }

        return rtn;
    }

    /**
     * Remove a tag from a document. This method gets called to remove the
     * "unprocessed" tag from a document after it's been processed by this code.
     *
     * @param doc
     *            the tagged document
     * @param unprocessed
     *            the tag to be removed
     * @param argMap
     *            the map of command-line argument values
     */
    private void removeTag(final TaggedDocument doc, final Tag unprocessed, final Map<ArgKey, String> argMap) {
        final WebTarget target = setUpRestCall(argMap,
                buildUrl(argMap, RestFunction.REMOVE_MAYAN_TAG.getFunction(doc.getId(), unprocessed.getId())));
        final Invocation.Builder ib = target.request(MediaType.APPLICATION_JSON);
        ib.delete();
    }

    /**
     * Set up a call to a REST service
     *
     * @param argMap
     *            the map of command-line arguments
     * @param fullUrl
     *            the full URL for the service
     *
     * @return the WebTarget object used to access the service
     */
    private WebTarget setUpRestCall(final Map<ArgKey, String> argMap, final String fullUrl) {
        return setUpRestCall(argMap, fullUrl, false);
    }

    /**
     * Set up a call to a REST service
     *
     * @param argMap
     *            the map of command-line arguments
     * @param fullUrl
     *            the full URL for the service
     * @param registerResponseFilter
     *            'true' means to attach a logging filter to the service
     *
     * @return the WebTarget object used to access the service
     */
    private WebTarget setUpRestCall(final Map<ArgKey, String> argMap, final String fullUrl,
            final boolean registerResponseFilter) {
        final HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                .credentials(argMap.get(ArgKey.MAYAN_USERID), argMap.get(ArgKey.MAYAN_PASSWORD)).build();
        final ClientConfig config = new ClientConfig();
        config.register(feature);
        if (registerResponseFilter) {
            config.register(ResponseFilter.class);
        }
        final Client client = ClientBuilder.newClient(config);

        final WebTarget target = client.target(fullUrl);
        return target;
    }

    /**
     * The allowed-metadata map is keyed by a document-type id (integer) and
     * contains a set of strings that represents the allowed-metadata labels for the
     * document type. This map is used to ensure that we don't try to add a new
     * metadata field to a document that doesn't allow that metadata type.
     *
     * @param doc
     *            the document in question
     * @param allowedMetadataMap
     *            the current allowed-metadata map
     * @param argMap
     *            the map of command-line arguments
     */
    private void updateAllowedMetadataMap(final TaggedDocument doc, final Map<Integer, Set<String>> allowedMetadataMap,
            final Map<ArgKey, String> argMap) {
        String nextUrl = buildUrl(argMap,
                RestFunction.LIST_ALLOWED_MAYAN_METADATA.getFunction(doc.getDocument_Type().getId()));
        final Set<String> typeSet = new HashSet<>();
        allowedMetadataMap.put(doc.getDocument_Type().getId(), typeSet);
        do {
            final MayanDocumentAllowedMetadata a = callApiGetter(MayanDocumentAllowedMetadata.class, nextUrl, argMap);
            for (final MayanDocumentMetadataType t : a.getResults()) {
                typeSet.add(t.getMetadata_type().getLabel());
            }
            nextUrl = a.getNext();
        } while (nextUrl != null);
    }

    /**
     * Check an extracted EXIF directory for errors. Throw a RuntimeException if
     * there is an error
     *
     * @param directory
     *            the directory to check
     */
    private void validateDirectory(final Directory directory) {
        if (directory.hasErrors()) {
            final StringBuilder buf = new StringBuilder();
            buf.append("Directory ");
            buf.append(directory.getName());
            buf.append(" ERRORS:");
            buf.append("\n");
            for (final String error : directory.getErrors()) {
                buf.append(error);
                buf.append("\n");
            }
            throw new RuntimeException(buf.toString());
        }
    }

}
