package com.loadburn.heron.complier;

import java.io.File;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-27
 */
public class FileTemplateSource implements TemplateSource {

    private File templateFile;

    public FileTemplateSource(File templateFile) {
        this.templateFile = templateFile;
    }

    @Override
    public String getLocation() {
        return templateFile.getAbsolutePath();
    }
}
