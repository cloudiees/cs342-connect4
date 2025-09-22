import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LinkedDS {
    public Message.Account account;
    public int theme;
    public ArrayList<Pair<String, String>> themes;
    LinkedDS(){
        super();
        themes = new ArrayList<>();
        themes.add(new Pair<>("Dark Mode",
                "-bgColor: #121212;" +
                        "-elevatedColor: #222222;" +
                        "-elevatedColor2: #3F3F3F;" +
                        "-elevatedColor3: #5F5F5F;" +
                        "-primary: #ff9eba;" +
                        "-secondary: #9ebaff;" +
                        "-fontColor: #EEEEEE;"));
        themes.add(new Pair<>("Light Mode",
                "-bgColor: white;" +
                        "-elevatedColor: #DDDDDD;" +
                        "-elevatedColor2: #BFBFBF;" +
                        "-elevatedColor3: #8F8F8F;" +
                        "-primary: #0000ff;" +
                        "-secondary: #ff0000;" +
                        "-fontColor: black;"));
        themes.add(new Pair<>("Rainbow Vomit",
                "-bgColor: #ff0000;" +
                        "-elevatedColor: #ffff00;" +
                        "-elevatedColor2: #00ff00;" +
                        "-elevatedColor3: #00ffff;" +
                        "-primary: #0000ff;" +
                        "-secondary: #ff00ff;" +
                        "-fontColor: white;"));
        theme = 0;
    }
}