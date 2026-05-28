package org.jerae.a1;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class A1API {

    private static final Pattern RGB_PATTERN = Pattern.compile("&#([a-fA-F0-9]{6})");
    private static final Pattern COLOR_PATTERN = Pattern.compile("&([0-9a-fA-F])");
    private static final Pattern FORMAT_PATTERN = Pattern.compile("&([k-oK-OrR])");

    public static Component colorize(CommandSender sender, String text) {
        boolean isConsole = sender instanceof ConsoleCommandSender;
        String processed = text;

        if (isConsole || sender.hasPermission("a1.chat.rgb")) {
            Matcher rgbMatcher = RGB_PATTERN.matcher(processed);
            while (rgbMatcher.find()) {
                String hex = rgbMatcher.group(1);
                processed = processed.replace("&#" + hex, "§#" + hex);
            }
        }

        Matcher colorMatcher = COLOR_PATTERN.matcher(processed);
        StringBuffer sbColor = new StringBuffer();
        while (colorMatcher.find()) {
            String colorCode = colorMatcher.group(1).toLowerCase();
            if (isConsole || sender.hasPermission("a1.chat.color." + colorCode)) {
                colorMatcher.appendReplacement(sbColor, "§" + colorCode);
            } else {
                colorMatcher.appendReplacement(sbColor, "&" + colorCode);
            }
        }
        colorMatcher.appendTail(sbColor);
        processed = sbColor.toString();

        Matcher formatMatcher = FORMAT_PATTERN.matcher(processed);
        StringBuffer sbFormat = new StringBuffer();
        while (formatMatcher.find()) {
            String formatCode = formatMatcher.group(1).toLowerCase();
            if (isConsole || sender.hasPermission("a1.chat.format." + formatCode)) {
                formatMatcher.appendReplacement(sbFormat, "§" + formatCode);
            } else {
                formatMatcher.appendReplacement(sbFormat, "&" + formatCode);
            }
        }
        formatMatcher.appendTail(sbFormat);
        processed = sbFormat.toString();

        return LegacyComponentSerializer.builder()
                .character('§')
                .hexColors()
                .build()
                .deserialize(processed);
    }
}
