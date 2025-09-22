package header;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

public class GameHeader extends Header{
    public GameHeader(String headerTxt) {
        super(headerTxt);
    }
    @Override
    public void leftItem(){
        leftButton = new Button();
    }
    @Override
    public void rightItemAndCreate(){
        ImageView imageView1 = new ImageView(new Image(getClass().getResourceAsStream("/images/white/menu.png")));
        imageView1.setFitHeight(40);
        imageView1.setFitWidth(40);
        leftButton.setGraphic(imageView1);
        invisSpacer = new Rectangle(51, 51);
        invisSpacer.getStyleClass().add("invisSpacer");
        hbox = new HBox(leftButton, spacer, header, spacer2, invisSpacer);
    }
}
