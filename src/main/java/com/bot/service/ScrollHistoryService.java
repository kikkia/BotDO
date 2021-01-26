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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScrollHistoryService {
    @Autowired
    private ScrollHistoryRepository hisoryRepository;

    public ScrollHistory save(User user, Map<Scroll, Integer> scrolls) {
        var history = new ScrollHistory(user, scrolls);
        return save(history);
    }

    public ScrollHistory save(ScrollHistory history) {
        return ScrollHistoryMapper.Companion.map(
                hisoryRepository.save(ScrollHistoryMapper.Companion.map(history)));
    }

    public List<ScrollHistory> getByUser(net.dv8tion.jda.api.entities.User user) {
       return hisoryRepository.findAllByUserId(user.getId()).stream()
               .map(ScrollHistoryMapper.Companion::map)
               .collect(Collectors.toList());
    }
}
