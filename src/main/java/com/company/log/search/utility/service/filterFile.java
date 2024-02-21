package com.company.log.search.utility.service;

import java.io.File;
import java.io.FileFilter;

public class filterFile implements FileFilter {

    @Override
    public boolean accept(File f) {
        return !f.isHidden() && !f.isDirectory() && f.canRead() && f.exists() && f.getName().toLowerCase().endsWith(".log");
    }
}
