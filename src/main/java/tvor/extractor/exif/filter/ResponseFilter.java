/**
 *
 */
package tvor.extractor.exif.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

/**
 * @author shore
 *
 */
public class ResponseFilter implements ClientResponseFilter {

    @Override
    public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext)
            throws IOException {
        System.out.println("===== Client request context =====");
        System.out.println("  Acceptable languages: " + requestContext.getAcceptableLanguages());
        System.out.println("  Acceptable media types: " + requestContext.getAcceptableMediaTypes());
        System.out.println("  Cookies: " + requestContext.getCookies());
        System.out.println("  Date: " + requestContext.getDate());
        System.out.println("  Headers: " + requestContext.getStringHeaders());
        System.out.println("  Language: " + requestContext.getLanguage());
        System.out.println("  Media type: " + requestContext.getMediaType());
        System.out.println("  Property names: " + requestContext.getPropertyNames());
        System.out.println("  Request method: " + requestContext.getMethod());
        System.out.println("  URI: " + requestContext.getUri());
        System.out.println("  Entity: " + requestContext.getEntity());
        System.out.println("  Entity annotations: " + Arrays.asList(requestContext.getEntityAnnotations()));
        System.out.println("  Entity class: " + requestContext.getEntityClass());
        System.out.println("  Entity type: " + requestContext.getEntityType());
        System.out.println();
        System.out.println("===== Client response context =====");
        System.out.println("  Allowed methods: " + responseContext.getAllowedMethods());
        System.out.println("  Cookies: " + responseContext.getCookies());
        System.out.println("  Date: " + responseContext.getDate());
        System.out.println("  Entity tag: " + responseContext.getEntityTag());
        System.out.println("  Headers: " + responseContext.getHeaders());
        System.out.println("  Language: " + responseContext.getLanguage());
        System.out.println("  Last modified: " + responseContext.getLastModified());
        System.out.println("  Length: " + responseContext.getLength());
        System.out.println("  Links: " + responseContext.getLinks());
        System.out.println("  Location: " + responseContext.getLocation());
        System.out.println("  Media type: " + responseContext.getMediaType());
        System.out.println("  Status: " + responseContext.getStatusInfo());
        System.out.println("  Status: " + responseContext.getStatus());
    }

}
