package com.company.log.search.utility.controller;

import com.company.log.search.utility.repository.LuceneIndexImpl;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class LuceneController {
    LuceneIndexImpl luceneIndex;

    @Autowired
    public LuceneController(LuceneIndexImpl luceneIndex){
        this.luceneIndex = luceneIndex;
    }


    @GetMapping("/{query}")
    public void search(@PathVariable(name = "query") String query) throws IOException, ParseException {
        List<String> documentNames = luceneIndex.search(query);
        for(String name:documentNames){
            System.out.println(name);
        }
    }

}
