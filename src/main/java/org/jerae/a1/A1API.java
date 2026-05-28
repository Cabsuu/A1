package org.jerae.a1;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class A1API {

    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<(.*?):(..*?)>");
    private static final Pattern RGB_PATTERN = Pattern.compile("&#([a-fA-F0-9]{6})");
    private static final Pattern COLOR_PATTERN = Pattern.compile("&([0-9a-fA-F])");
    private static final Pattern FORMAT_PATTERN = Pattern.compile("&([k-oK-OrR])");

    private static String getColorName(String code) {
        switch (code.toLowerCase()) {
            case "0": return "black";
            case "1": return "dark_blue";
            case "2": return "dark_green";
            case "3": return "dark_aqua";
            case "4": return "dark_red";
            case "5": return "dark_purple";
            case "6": return "gold";
            case "7": return "gray";
            case "8": return "dark_gray";
            case "9": return "blue";
            case "a": return "green";
            case "b": return "aqua";
            case "c": return "red";
            case "d": return "light_purple";
            case "e": return "yellow";
            case "f": return "white";
            default: return null;
        }
    }

    private static String getFormatName(String code) {
        switch (code.toLowerCase()) {
            case "k": return "obfuscated";
            case "l": return "bold";
            case "m": return "strikethrough";
            case "n": return "underlined";
            case "o": return "italic";
            case "r": return "reset";
            default: return null;
        }
    }

    private static String parseColorOrHex(String input) {
        if (input.startsWith("&") && input.length() == 2) {
            String name = getColorName(input.substring(1));
            if (name != null) return name;
        }
        if (input.startsWith("#") && input.length() == 7) {
            return input;
        }
        return input;
    }

    public static Component colorize(CommandSender sender, String text) {
        boolean isConsole = sender instanceof ConsoleCommandSender;

        // 1. Manually escape all existing MiniMessage tags to prevent permission escalation.
        // MiniMessage escapeTags replaces `<` with `\<` if it thinks it's a valid tag.
        // However, `<&b:&9>` is NOT a valid MiniMessage tag, so it doesn't escape it!
        // To be absolutely certain, we should just use a strict tag resolver instead of string escaping,
        // or manually replace `<` with `\<` and then do our replacements.
        // Wait, the safest way is to build the Component directly without MiniMessage interpolation of user string,
        // BUT the user wants the chat to have gradients.
        // If we escape everything manually: `text.replace("<", "\\<")`, then `<&b:&9>` becomes `\<\&b:\&9>`.
        String processed = text.replace("<", "\\<");

        Pattern escapedGradientPattern = Pattern.compile("\\\\<(.*?):(.*?)>");
        if (isConsole || sender.hasPermission("a1.chat.gradient")) {
            Matcher gradientMatcher = escapedGradientPattern.matcher(processed);
            StringBuffer sbGradient = new StringBuffer();
            while (gradientMatcher.find()) {
                String c1 = parseColorOrHex(gradientMatcher.group(1));
                String c2 = parseColorOrHex(gradientMatcher.group(2));
                gradientMatcher.appendReplacement(sbGradient, "<gradient:" + c1 + ":" + c2 + ">");
            }
            gradientMatcher.appendTail(sbGradient);
            processed = sbGradient.toString();
        }

        Matcher rgbMatcher = RGB_PATTERN.matcher(processed);
        StringBuffer sbRgb = new StringBuffer();
        while (rgbMatcher.find()) {
            String hex = rgbMatcher.group(1);
            if (isConsole || sender.hasPermission("a1.chat.rgb")) {
                rgbMatcher.appendReplacement(sbRgb, "<#" + hex + ">");
            } else {
                rgbMatcher.appendReplacement(sbRgb, "&#" + hex);
            }
        }
        rgbMatcher.appendTail(sbRgb);
        processed = sbRgb.toString();

        Matcher colorMatcher = COLOR_PATTERN.matcher(processed);
        StringBuffer sbColor = new StringBuffer();
        while (colorMatcher.find()) {
            String colorCode = colorMatcher.group(1).toLowerCase();
            String colorName = getColorName(colorCode);
            if (isConsole || sender.hasPermission("a1.chat.color." + colorName)) {
                colorMatcher.appendReplacement(sbColor, "<" + colorName + ">");
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
            String formatName = getFormatName(formatCode);
            if (isConsole || sender.hasPermission("a1.chat.format." + formatName)) {
                formatMatcher.appendReplacement(sbFormat, "<" + formatName + ">");
            } else {
                formatMatcher.appendReplacement(sbFormat, "&" + formatCode);
            }
        }
        formatMatcher.appendTail(sbFormat);
        processed = sbFormat.toString();

        return MiniMessage.miniMessage().deserialize(processed);
    }
}
