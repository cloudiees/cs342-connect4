package header;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class SettingsHeader extends Header{
    public Button rightButton;
    public SettingsHeader(String headerTxt) {
        super(headerTxt);
    }
    @Override
    public void rightItemAndCreate(){
        rightButton = new Button();
        ImageView imageView1 = new ImageView(new Image(getClass().getResourceAsStream("/images/white/back.png")));
        imageView1.setFitHeight(40);
        imageView1.setFitWidth(40);
        ImageView imageView2 = new ImageView(new Image(getClass().getResourceAsStream("/images/white/logout.png")));
        imageView2.setFitHeight(40);
        imageView2.setFitWidth(40);
        leftButton.setGraphic(imageView1);
        rightButton.setGraphic(imageView2);
        hbox = new HBox(leftButton, spacer, header, spacer2, rightButton);
    }
}
