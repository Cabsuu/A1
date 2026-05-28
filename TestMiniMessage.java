import net.kyori.adventure.text.minimessage.MiniMessage;
public class TestMiniMessage {
    public static void main(String[] args) {
        String text = "<&b:&9>Hello <red>world!";
        String processed = MiniMessage.miniMessage().escapeTags(text);
        System.out.println("After escapeTags: " + processed);
    }
}
