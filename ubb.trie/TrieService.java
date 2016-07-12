package com.kk.sms.trie;

import com.kk.sms.service.BannedWordService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class TrieService {
    private Log logger = LogFactory.getLog(TrieService.class);
    TrieTree tree = new TrieTree();

    @Autowired
    BannedWordService bannedWordService;

    @PostConstruct
    private void init() {
        _init();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                _init();
            }
        }, 1000 * 60 * 30, 1000 * 60 * 30);// task delay,period 30分钟
    }

    private void _init() {
        List<String> list = bannedWordService.getAllBannedWord();
        TrieTree t = new TrieTree();
        for (String word : list) {
            t.add(word);
        }
        t.compile();
        synchronized (tree) {
            tree = t;
        }
        logger.info("trie build done!");
    }

    public boolean containWord(String content) {
        boolean ret = tree.containKeyWord(content);
        return ret;
    }

}
