import header.SettingsHeader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SettingsScene extends SceneBasic {
    public SettingsHeader header;
    public Button theme;

    public SettingsScene(LinkedDS globalObjs) {
        TextField themeSelector = new TextField();
        themeSelector.setText("Click to change Theme:");
        themeSelector.setEditable(false);
        themeSelector.getStyleClass().add("miniHeader");
        this.globalObjs = globalObjs;
        header = new SettingsHeader("Settings");
        root = new BorderPane();
        root.setTop(header.hbox);
        theme = new Button();
        theme.getStyleClass().add("home-button");
        theme.prefWidthProperty().bind(root.widthProperty().multiply(0.75));
        VBox vbox = new VBox(10, themeSelector,theme, generateRectangleSpacer(40, 40));
        vbox.setAlignment(Pos.CENTER);
        root.setCenter(vbox);
        root.getStyleClass().add("borderpane");
    }

    @Override
    public void updateSceneImgs(boolean dark) {
        String path = "images/";
        if(dark) {path += "black/";}
        else {path += "white/";}
        header.leftButton.setGraphic(generateImageView((path + "back.png"), 40, 40));
        header.rightButton.setGraphic(generateImageView((path + "logout.png"), 40, 40));
    }
}
