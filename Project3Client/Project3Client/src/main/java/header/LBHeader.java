package header;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class LBHeader extends Header {
    public Button refresh;
    public LBHeader(String headerTxt) {
        super(headerTxt);
    }
    @Override
    public void rightItemAndCreate(){
        refresh = new Button();
        ImageView imageView1 = new ImageView(new Image(getClass().getResourceAsStream("/images/white/back.png")));
        imageView1.setFitHeight(40);
        imageView1.setFitWidth(40);
        ImageView imageView2 = new ImageView(new Image(getClass().getResourceAsStream("/images/white/refresh.png")));
        imageView2.setFitHeight(40);
        imageView2.setFitWidth(40);
        leftButton.setGraphic(imageView1);
        refresh.setGraphic(imageView2);
        hbox = new HBox(leftButton, spacer, header, spacer2, refresh);
    }
}
