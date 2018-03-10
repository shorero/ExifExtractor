# ExifExtractor

The EXIF Extractor is a Java application designed to extract EXIF metadata from images stored in Mayan-EDMS
and convert them into Mayan-EDMS metadata fields. The application requires Java 1.8 or later. 
The application may be run via the command

>  java -jar exif-1.1.0.jar optional-command-parameters

To see the switches that may appear in the optional command parameters, use the command:
> java -jar exif-1.1.0.jar -h

If you specify no parameters on the command line, the application prompts you for all the information it needs. 
The required data include:

* Mayan userid and password. Note that the specified userid must have permissions to create metadata types
  and to associated metadata values with documents in addition to read-only permissions like list all
  tags and download documents.
* The base URL used to connect to Mayan-EDMS. This might be something like http://some.domain.name:29880
* The Mayan-EDMS tag that marks documents to be processed. I use tag label "Unprocessed", but you 
  can specify any tag to the app. The application processes only documents carrying the indicated tag
  and removes the tag when it finishes with the document. In addition to carrying the expected tag, the
  document must have an "image/*" mime type, as reported by Mayan-EDMS.
* A flag, either true or false (lower case, spelled out) that indicates whether the application should insert new
  Mayan metadata types as part of its operation. Note that all the Mayan metadata types must exist in the targt
  Mayan database; if any are missing, the application throws an exception. Also note that you have no
  control over the mapping between the EXIF metadata names and the Mayan metadata labels; the mapping is
  hard-coded into the application.
* A flag, either true or false (lower case, spelled out) that indicates whether the application should
  ignore EXIF fields for which it has no mapping. The application knows about 50 or more standard EXIF
  field names, but it hasn't been tested with a wide variety of cameras; there are probably EXIF fields out
  there that the application isn't expecting. By default the application throws an exception when it encounters
  an unexpected EXIF field, but setting this flag 'true' causes the app to ignore quietly any unexpected field.

Note that the application requires that *all* Mayan metadata types are defined in the Mayan database. However,
since these are normal Mayan metadata types, you can configure your document types to allow only a subset
of all the EXIF metadata types. The application does not attempt to add a metadata field to a document
that doesn't allow that field. The application also does not add a metadata field to a document which already
has a value for that field.
