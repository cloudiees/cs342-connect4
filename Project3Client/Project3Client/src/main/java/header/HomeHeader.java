package header;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class HomeHeader extends Header {
    public Button settingsButton;
    public HomeHeader(String headerTxt) {
        super(headerTxt);
    }
    @Override
    public void rightItemAndCreate(){
        settingsButton = new Button();
        ImageView imageView1 = new ImageView(new Image(getClass().getResourceAsStream("/images/white/close.png")));
        imageView1.setFitHeight(40);
        imageView1.setFitWidth(40);
        ImageView imageView2 = new ImageView(new Image(getClass().getResourceAsStream("/images/white/settings.png")));
        imageView2.setFitHeight(40);
        imageView2.setFitWidth(40);
        leftButton.setGraphic(imageView1);
        settingsButton.setGraphic(imageView2);
        hbox = new HBox(leftButton, spacer, header, spacer2, settingsButton);
    }
}
