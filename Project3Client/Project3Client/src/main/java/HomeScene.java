import header.HomeHeader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class HomeScene extends SceneBasic {
    public HomeHeader header;
    public Button findMatch;
    public Button playSolo;
    public Button leaderboard;
    public TextField user;

    @Override
    public void updateSceneImgs(boolean dark) {
        String path = "images/";
        if(dark) {path += "black/";}
        else {path += "white/";}
        header.leftButton.setGraphic(generateImageView((path + "close.png"), 40, 40));
        header.settingsButton.setGraphic(generateImageView((path + "settings.png"), 40, 40));
    }

    private class HomeButton extends Button {
        public HomeButton(String text) {
            super(text);
            this.getStyleClass().add("home-button");
            this.prefWidthProperty().bind(root.widthProperty().multiply(0.75));
        }
    }

    public HomeScene(LinkedDS globalObjs) {
        this.globalObjs = globalObjs;
        root = new BorderPane();
        header = new HomeHeader("Connect 4");
        findMatch = new HomeButton("Find Match");
        playSolo = new HomeButton("Play Solo");
        leaderboard = new HomeButton("Leaderboard");
        VBox centerVbox = new VBox(generateVSpacer(), findMatch, generateVSpacer(), playSolo, generateVSpacer(), leaderboard, generateVSpacer());
        centerVbox.setAlignment(Pos.CENTER);
        user = new TextField();
        user.setEditable(false);
        user.getStyleClass().add("username");
        root.setTop(header.hbox);
        root.setCenter(centerVbox);
        root.setBottom(user);
        root.getStyleClass().add("borderpane");
    }
}
