package com.company.log.search.utility.service;

import com.company.log.search.utility.model.LuceneConstants;
import com.company.log.search.utility.repository.LuceneIndexImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DirectoryMonitoringService {

    private LuceneIndexImpl luceneIndexImpl;

    @Autowired
    public DirectoryMonitoringService(@Value("${lucene.index.location}") String watchPath, LuceneIndexImpl luceneIndexImpl) throws Exception {
        Path path = Paths.get("Indexes");
        this.luceneIndexImpl=luceneIndexImpl;
        File parentDirectory = FileUtils.getFile(watchPath);
        FileAlterationObserver fileAlterationObserver = new FileAlterationObserver(parentDirectory, new filterFile());
        fileAlterationObserver.addListener(new FileAlterationListener() {
            @Override
            public void onDirectoryChange(File file) {

            }

            @Override
            public void onDirectoryCreate(File file) {

            }

            @Override
            public void onDirectoryDelete(File file) {

            }

            @Override
            public void onFileChange(File file) {
                    System.out.println("Changing new File...");
                    onFileDelete(file);
                    onFileCreate(file);
            }

            @Override
            public void onFileCreate(File file) {
                    System.out.println("Indexing new File...");
                    try {
                        Document document = luceneIndexImpl.getDocument(file);
                        luceneIndexImpl.getWriter().addDocument(document);
                        luceneIndexImpl.getWriter().commit();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
            }

            @Override
            public void onFileDelete(File file) {
                    System.out.println("Deleting new File...");
                    try {
                        luceneIndexImpl.getWriter().deleteDocuments(new Term(LuceneConstants.FILE_NAME));
                        luceneIndexImpl.getWriter().commit();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
            }

            @Override
            public void onStart(FileAlterationObserver fileAlterationObserver) {

            }

            @Override
            public void onStop(FileAlterationObserver fileAlterationObserver) {

            }
        });

        FileAlterationMonitor monitor = new FileAlterationMonitor(500, fileAlterationObserver);
        monitor.start();
    }
}
