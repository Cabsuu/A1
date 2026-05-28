package org.jerae.a1;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class A1APITest {

    @Test
    public void testConsoleCommandSender() {
        ConsoleCommandSender console = mock(ConsoleCommandSender.class);

        Component result = A1API.colorize(console, "&aHello &#FF0000World &lBold");

        String legacy = LegacyComponentSerializer.builder().character('§').hexColors().build().serialize(result);
        assertEquals("§aHello §#ff0000World §lBold", legacy);
    }

    @Test
    public void testPlayerWithNoPermissions() {
        CommandSender player = mock(CommandSender.class);

        Component result = A1API.colorize(player, "&aHello &#FF0000World &lBold");

        String legacy = LegacyComponentSerializer.builder().character('§').hexColors().build().serialize(result);
        assertEquals("&aHello &#FF0000World &lBold", legacy);
    }

    @Test
    public void testPlayerWithRGBPermission() {
        CommandSender player = mock(CommandSender.class);
        when(player.hasPermission("a1.chat.rgb")).thenReturn(true);

        Component result = A1API.colorize(player, "&aHello &#FF0000World &lBold");

        String legacy = LegacyComponentSerializer.builder().character('§').hexColors().build().serialize(result);
        assertEquals("&aHello §#ff0000World &lBold", legacy);
    }

    @Test
    public void testPlayerWithColorPermission() {
        CommandSender player = mock(CommandSender.class);
        when(player.hasPermission("a1.chat.color.green")).thenReturn(true);
        when(player.hasPermission("a1.chat.color.aqua")).thenReturn(false);

        Component result = A1API.colorize(player, "&aGreen &bBlue");

        String legacy = LegacyComponentSerializer.builder().character('§').hexColors().build().serialize(result);
        assertEquals("§aGreen &bBlue", legacy);
    }

    @Test
    public void testPlayerWithFormatPermission() {
        CommandSender player = mock(CommandSender.class);
        when(player.hasPermission("a1.chat.format.bold")).thenReturn(true);
        when(player.hasPermission("a1.chat.format.strikethrough")).thenReturn(false);

        Component result = A1API.colorize(player, "&lBold &mStrikethrough");

        String legacy = LegacyComponentSerializer.builder().character('§').hexColors().build().serialize(result);
        assertEquals("§lBold &mStrikethrough", legacy);
    }

    @Test
    public void testPlayerWithGradientPermission() {
        CommandSender player = mock(CommandSender.class);
        when(player.hasPermission("a1.chat.gradient")).thenReturn(true);

        Component result = A1API.colorize(player, "<&b:&9>Hello world!");

        // Converting an output with gradient back to legacy format usually won't output the gradient.
        // It outputs the string correctly formatted in legacy.
        // Let's assert that the MiniMessage syntax was properly parsed into standard components.
        // For simplicity, we can check if it converts to legacy colors representing the gradient,
        // or just plain text if gradient is not preserved via legacy serializer.
        // MiniMessage -> LegacySerializer downsamples hex to nearest legacy if hexColors=false,
        // but since we enabled hexColors, it outputs §#RRGGBB

        String legacy = LegacyComponentSerializer.builder().character('§').hexColors().build().serialize(result);
        // We know the first character will be colored aqua (#55FFFF) or similar
        // Just verify it doesn't contain the raw gradient string
        System.out.println("Gradient test legacy string: " + legacy);
        assertEquals(false, legacy.contains("<&b:&9>"));
        // MiniMessage splits characters and colors them individually
        // Ex: "H", "e", "l", "l", "o"
        // We will assert that the text is ungarbled if we remove color codes.
        String stripped = legacy.replaceAll("§x(§[0-9a-fA-F]){6}", "").replaceAll("§#[0-9a-fA-F]{6}", "").replaceAll("§[0-9a-fA-Fk-orK-OR]", "").replace("\\<", "<").replace("\\>", ">");
        assertEquals("Hello world!", stripped);
    }

    @Test
    public void testPlayerWithoutGradientPermission() {
        CommandSender player = mock(CommandSender.class);
        when(player.hasPermission("a1.chat.gradient")).thenReturn(false);

        Component result = A1API.colorize(player, "<&b:&9>Hello world!");

        String legacy = LegacyComponentSerializer.builder().character('§').hexColors().build().serialize(result);
        assertEquals("<&b:&9>Hello world!", legacy);
    }
}
