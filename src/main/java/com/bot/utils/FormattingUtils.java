package com.bot.utils;

import com.bot.db.entities.ScrollGroup;
import com.bot.db.entities.UserEntity;
import com.bot.db.mapper.ScrollInventoryMapper;
import com.bot.models.Scroll;
import com.bot.models.ScrollInventory;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.data.DataHolder;
import gui.ava.html.image.generator.HtmlImageGenerator;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FormattingUtils {
    final private static DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(false,
            Extensions.TABLES
    );

    private static ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
    private static Parser parser = Parser.builder(OPTIONS).build();
    private static HtmlRenderer htmlRenderer = HtmlRenderer.builder(OPTIONS).build();

    public static File scrollGroupToImage(ScrollGroup scrollGroup) throws IOException {
        var markdown = scrollGroupToMarkdown(scrollGroup);
        var html = markdownToHtml(markdown);
        // Append a link to CSS for the html
        html = injectCss(html, "css/scrollgroup.css");
        var image = removeCssImmuneBorder(htmlToImage(html));
        var file = new File(getNewImageFileName(scrollGroup));
        // Save image to disk
        ImageIO.write(image, "png", file);
        // Delete image after 10
        cleanupExecutor.schedule(file::delete, 10, TimeUnit.SECONDS);
        return file;
    }

    private static String scrollGroupToMarkdown(ScrollGroup scrollGroup) {
        var sb = new StringBuilder();
        //sb.append("## Scroll Group: ").append(scrollGroup.getName()).append("\n");

        // Map users to scroll inventories
        Map<String, ScrollInventory> inventories = scrollGroup.getUsers().stream()
                .collect(Collectors.toMap(
                        UserEntity::getEffectiveName,
                        u -> ScrollInventoryMapper.Companion.map(u.getInventory())));

        Map<Scroll, Integer> totals = new HashMap<>();
        Set<Scroll> scrolls = new HashSet<>();
        for (ScrollInventory scrollInventory : inventories.values()) {
            // Add all scrolls to set where there are more than 1 of them in an inventory
            scrolls.addAll(new HashSet<>(scrollInventory
                    .getScrolls().entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet())));
        }

        sb.append("| Family name |");
        for (Scroll s : scrolls) {
            sb.append(s.getDisplayName()).append(" | ");
        }
        sb.setLength(sb.length()); // Remove last | from scroll names
        sb.append(" \n|");
        sb.append(" ---- |".repeat(Math.max(0, scrolls.size() + 1)));
        sb.append(" \n| ");

        for (Map.Entry<String, ScrollInventory> e : inventories.entrySet()) {
            sb.append(e.getKey()).append(" | ");
            for (Scroll s : scrolls) {
                int count = e.getValue().getScrollCount(s);
                sb.append(count).append(" | ");
                // Update totals
                totals.computeIfPresent(s, (key, tot) -> tot + count);
                totals.putIfAbsent(s, count);
            }
            sb.append("\n|");
        }
        sb.append("Total|");
        for (Scroll s: scrolls) {
            sb.append(totals.get(s)).append("|");
        }

        return sb.toString();
    }

    private static String markdownToHtml(String markdown) {
        Node doc = parser.parse(markdown);
        return htmlRenderer.render(doc);
    }

    private static BufferedImage htmlToImage(String html) {
        HtmlImageGenerator generator = new HtmlImageGenerator();
        generator.loadHtml(html);
        return generator.getBufferedImage();
    }

    private static String injectCss(String html, String CSSPath) throws IOException {
        String css = Files.readString(Path.of(CSSPath));
        return "<style>" + css + "</style>" + html;
    }

    private static String getNewImageFileName(ScrollGroup scrollGroup) {
        return scrollGroup.getName() + Instant.now().toEpochMilli() + ".png";
    }

    private static BufferedImage removeCssImmuneBorder(BufferedImage src) {
        return src.getSubimage(5, 5, src.getWidth()-10, src.getHeight()-10);
    }

    public static String getFormattedTimestamp(String input) {
        // Split into parts from spaces
        String[] parts = input.split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException();
        }
        return parts[0] + "'T'" + parts[1] + parts[2];
    }
}
