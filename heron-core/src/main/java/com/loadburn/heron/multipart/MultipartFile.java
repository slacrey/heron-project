package com.loadburn.heron.multipart;

import com.google.inject.ImplementedBy;
import com.loadburn.heron.multipart.commons.CommonsMultipartFile;
import org.apache.commons.fileupload.FileItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-7
 */
public interface MultipartFile {

    String getName();

    String getOriginalFilename();

    String getContentType();

    boolean isEmpty();

    long getSize();

    byte[] getBytes() throws IOException;

    InputStream getInputStream() throws IOException;

    void transferTo(File dest) throws IOException, IllegalStateException;

}
