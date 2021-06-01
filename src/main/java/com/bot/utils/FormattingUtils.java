package com.bot.utils;

import com.bot.db.entities.*;
import com.bot.db.mapper.ScrollInventoryMapper;
import com.bot.models.Scroll;
import com.bot.models.ScrollInventory;
import com.bot.models.WarNode;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.w3c.dom.Document;
import org.xhtmlrenderer.simple.Graphics2DRenderer;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.List;
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

    public static File scrollGroupToImage(ScrollGroup scrollGroup) throws Exception {
        var markdown = scrollGroupToMarkdown(scrollGroup);
        return markdownToFile(markdown, "css/scrollgroup.css");
    }

    public static File eventsToImage(List<EventEntity> eventEntities) throws Exception {
        var markdown = eventsToMarkdown(eventEntities);
        return markdownToFile(markdown, "css/scrollgroup.css");
    }

    private static File markdownToFile(String markdown, String cssPath) throws Exception {
        var html = markdownToHtml(markdown);
        html = injectCss(html, cssPath);
        html = injectBoilerplate(html);
        var image = cropToFit(htmlToImage(html));
        var file = new File(getNewImageFileName());
        // Save image to disk
        ImageIO.write(image, "png", file);
        // Delete image after 10
        cleanupExecutor.schedule(file::delete, 10, TimeUnit.SECONDS);
        return file;
    }

    private static String injectBoilerplate(String html) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html lang=\"en\">");
        sb.append(html);
        sb.append("</html>");
        return sb.toString();
    }

    // Tables have dynamic height and image is fixed, find the first alpha pixel and crop to height
    private static BufferedImage cropToFit(BufferedImage image) {
        for (int i = 0; i < image.getHeight(); i++) {
            Color c = new Color(image.getRGB(image.getWidth()/2, i), true);
            if (c.getAlpha() < 255) {
                return image.getSubimage(0, 0, image.getWidth(), i);
            }
        }
        return image;
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

    private static String eventsToMarkdown(List<EventEntity> eventEntities) {
        var sb = new StringBuilder();
        sb.append("| Event name | Time till event |\n").append("| ---- | ---- |\n| ");

        for (EventEntity e : eventEntities) {
            sb.append(e.getName())
                    .append(" | ")
                    .append(FormattingUtils.prettyPrintDuration(e.getDurationUntilEvent())).append(" |\n");
        }
        return sb.toString();
    }

    private static String markdownToHtml(String markdown) {
        Node doc = parser.parse(markdown);
        return htmlRenderer.render(doc);
    }

    private static BufferedImage htmlToImage(String html) throws ParserConfigurationException, IOException, SAXException {
        // Convert html to doc for rendering
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document htmlDoc = documentBuilder.parse(new ByteArrayInputStream(html.getBytes()));
        Graphics2DRenderer g2r = new Graphics2DRenderer();
        g2r.setDocument(htmlDoc, "http://localhost:42069/");
        Dimension dim = new Dimension(1920, 1080);
        BufferedImage buff = new BufferedImage((int)dim.getWidth(), (int)dim.getHeight(), 2);
        Graphics2D g = (Graphics2D)buff.getGraphics();
        g2r.layout(g, dim);
        g2r.render(g);
        g.dispose();
        return buff;
    }

    private static String injectCss(String html, String CSSPath) throws IOException {
        return "<head><link rel=\"stylesheet\" href=\"" + CSSPath + "\"></link></head>" + html;
    }

    private static String getNewImageFileName(ScrollGroup scrollGroup) {
        return scrollGroup.getName() + Instant.now().toEpochMilli() + ".png";
    }

    private static String getNewImageFileName() {
        return "events" + Instant.now().toEpochMilli() + ".png";
    }

    private static BufferedImage removeCssImmuneBorder(BufferedImage src) {
        return src.getSubimage(5, 5, src.getWidth()-10, src.getHeight()-10);
    }

    public static String prettyPrintDuration(Duration d) {
        String daySuffix = d.getStandardDays() == 1 ? " day " : " days ";
        String hourSuffix = d.getStandardHours() % 24 == 1 ? " hour " : " hours ";
        String minSuffix = d.getStandardMinutes() % 60 == 1 ? " minute " : " minutes ";
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix(daySuffix)
                .appendHours()
                .appendSuffix(hourSuffix)
                .appendMinutes()
                .appendSuffix(minSuffix)
                .toFormatter();
        Period p = new Period();
        // Can't get it to format correctly with the d.toPeriod
        p = p.plusDays(d.toStandardDays().getDays());
        p = p.plusHours(d.toStandardHours().getHours() % 24);
        p = p.plusMinutes(d.toStandardMinutes().getMinutes() % 60);
        return formatter.print(p);
    }

    public static String generateWelcomeMessage(GuildMemberJoinEvent event, String message) {
        return message.replaceAll("%name%", event.getMember().getEffectiveName());
    }

    public static String getEnhancementString(int level) {
        if (level <= 15) {
            return "+" + level;
        } else if (level == 16) {
            return "PRI";
        } else if (level == 17) {
            return "DUO";
        } else if (level == 18) {
            return "TRI";
        } else if (level == 19) {
            return "TET";
        } else if (level == 20) {
            return "PEN";
        } else {
            return "wtf is this? " + level;
        }
    }

    public static String formatDateToBasicString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("E dd-MMM", Locale.ENGLISH);
        return formatter.format(date);
    }

    // TODO: Clean this up with archived builder
    public static MessageEmbed generateWarMessage(WarEntity warEntity) {
        var limit = warEntity.getWarNode() == null ? 100: warEntity.getWarNode().getCap();
        var attendees = warEntity.getAttendees().stream()
                .sorted(warSignupComparator)
                .collect(Collectors.toList());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(formatDateToBasicString(warEntity.getWarTime()) + " war signup.");
        embedBuilder.addField("Avg gearscore", String.valueOf(warEntity.getAverageGS()), true);
        embedBuilder.addField("Total signups (Maybes included)", String.valueOf(warEntity.getAttendees().size()), true);
        embedBuilder.addBlankField(true);
        if (warEntity.getWarNode() != null) {
            embedBuilder.addField("Node", warEntity.getWarNode().getDisplayName(), true);
            embedBuilder.addField("Tier", warEntity.getWarNode().getTier().getDisplay(), true);
            embedBuilder.addField("Cap", String.valueOf(warEntity.getWarNode().getCap()), true);
        } else {
            embedBuilder.addField("Node", "No node set, you can set one by using the ,node command", false);
        }

        embedBuilder.setDescription(buildAttendeeList(attendees, limit));
        embedBuilder.setFooter("To sign up react `Y` for yes, `N` for no. ? for maybe. War Id: " + warEntity.getId());
        return embedBuilder.build();
    }

    public static MessageEmbed generateArchivedWarMessage(WarEntity warEntity,
                                                          List<WarVodEntity> vods,
                                                          List<WarStatsEntity> stats) {
        var limit = warEntity.getWarNode() == null ? 100: warEntity.getWarNode().getCap();
        var attendees = warEntity.getAttendees().stream()
                .filter(it -> !it.getNoShow())
                .sorted(warSignupComparator)
                .collect(Collectors.toList());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(formatDateToBasicString(warEntity.getWarTime()) + " war results.");
        embedBuilder.addField("Avg gearscore", String.valueOf(warEntity.getAverageGS()), true);
        embedBuilder.addField("Total Attendance (No shows removed)", String.valueOf(attendees.size()), true);
        embedBuilder.addField("Win", String.valueOf(warEntity.getWon()), true);
        if (warEntity.getWarNode() != null) {
            embedBuilder.addField("Node", warEntity.getWarNode().getDisplayName(), true);
            embedBuilder.addField("Tier", warEntity.getWarNode().getTier().getDisplay(), true);
            embedBuilder.addField("Cap", String.valueOf(warEntity.getWarNode().getCap()), true);
        } else {
            embedBuilder.addField("Node", "No node set, you can set one by using the ,node command", false);
        }
        if (!vods.isEmpty()) {
            embedBuilder.addField("Vods", generateVodsField(vods), false);
        }
        if (!stats.isEmpty()) {
            embedBuilder.addField("Stats", generateStatsField(stats), false);
        }
        embedBuilder.setDescription(buildAttendeeList(attendees, limit));
        embedBuilder.setFooter("War Id: " + warEntity.getId());
        return embedBuilder.build();
    }

    private static String generateVodsField(List<WarVodEntity> vods) {
        StringBuilder builder = new StringBuilder();
        for (WarVodEntity vod : vods) {
            builder.append(vod.toEmbed()).append(" ");
        }
        return builder.toString();
    }

    private static String generateStatsField(List<WarStatsEntity> stats) {
        StringBuilder builder = new StringBuilder();
        for (WarStatsEntity stat: stats) {
            builder.append(stat.toEmbed()).append(" ");
        }
        return builder.toString();
    }

    private static String buildAttendeeList(List<WarAttendanceEntity> attendees, int limit) {
        var count = 0; // Used to keep track for when we hit the limit
        var maybe = false; // Denotes if we have hit the maybe category yet
        if (attendees.isEmpty()) {
            return "No attendees yet";
        }
        StringBuilder toReturn = new StringBuilder("```css\n");

        for (WarAttendanceEntity attendee : attendees) {
            if (attendee.getMaybe() && !maybe) { // Maybes are always last on the list
                toReturn.append("--Maybe--\n");
                maybe = true;
            }
            if (count == limit && !maybe) { // If we are at limit and not in the maybes yet
                toReturn.append("--BACKUPS--\n");
            }
            toReturn.append(attendee.toMessageEntry()).append("\n");
            count++;
        }
        toReturn.append("```");
        return toReturn.toString();
    }

    private static final Comparator<WarAttendanceEntity> warSignupComparator = (o1, o2) -> {
        if (o1.getMaybe() && !o2.getMaybe()) {
            return 1;
        } else if (!o1.getMaybe() && o2.getMaybe()) {
            return -1;
        }
        return o1.getCreated().compareTo(o2.getCreated());
    };
}
