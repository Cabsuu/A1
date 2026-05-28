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
        when(player.hasPermission("a1.chat.color.a")).thenReturn(true);
        when(player.hasPermission("a1.chat.color.b")).thenReturn(false);

        Component result = A1API.colorize(player, "&aGreen &bBlue");

        String legacy = LegacyComponentSerializer.builder().character('§').hexColors().build().serialize(result);
        assertEquals("§aGreen &bBlue", legacy);
    }

    @Test
    public void testPlayerWithFormatPermission() {
        CommandSender player = mock(CommandSender.class);
        when(player.hasPermission("a1.chat.format.l")).thenReturn(true);
        when(player.hasPermission("a1.chat.format.m")).thenReturn(false);

        Component result = A1API.colorize(player, "&lBold &mStrikethrough");

        String legacy = LegacyComponentSerializer.builder().character('§').hexColors().build().serialize(result);
        assertEquals("§lBold &mStrikethrough", legacy);
    }
}
