package com.bot.service;

import com.bot.db.entities.User;
import com.bot.db.mapper.ScrollHistoryMapper;
import com.bot.db.mapper.ScrollInventoryMapper;
import com.bot.db.repositories.ScrollHistoryRepository;
import com.bot.models.Scroll;
import com.bot.models.ScrollHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

@Service
@Transactional
public class ScrollHistoryService {
    @Autowired
    private ScrollHistoryRepository hisoryRepository;

    public ScrollHistory save(User user, Map<Scroll, Integer> scrolls) {
        var history = new ScrollHistory(user, scrolls);
        return ScrollHistoryMapper.Companion.map(
                hisoryRepository.save(ScrollHistoryMapper.Companion.map(history)));
    }
}
