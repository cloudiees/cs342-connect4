import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import static java.lang.Thread.sleep;

public class GuiClient extends Application{


	Client clientConnection;

	LinkedDS globalObjs;

	HomeScene homeScene;
	SettingsScene settingsScene;
	LBScene lbScene;
	MMScene mmScene;
	GameScene gameScene;
	StackPane root;
	CountDownLatch turnLatch;
	FFRemoverTool ffRemoverTool;
	GregBot greg;
	boolean multi;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		clientConnection = new Client();
		clientConnection.start();
		globalObjs = new LinkedDS();
		Scene scene = innitScenes();
		createButtonHandlers();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		primaryStage.setScene(scene);
		primaryStage.setTitle("Client");
		primaryStage.show();
		primaryStage.setMinHeight(300);
		primaryStage.setMinWidth(300);
		primaryStage.setHeight(700);
		primaryStage.setWidth(1000);
		homeScene.root.toFront();
		loginAccount();
	}

	private Scene innitScenes(){
		createScenes();
		settingsScene.theme.setText(globalObjs.themes.get(globalObjs.theme).getKey());
		gameScene.board.board.maxWidthProperty().bind(root.widthProperty().multiply(.75));
		root.getStylesheets().add(getClass().getResource("/main.css").toExternalForm());
        return new Scene(root, 800,500);
	}

	private void chatMessage(){
		String chatMsg = globalObjs.account.username + ": " + gameScene.message.getText();
		gameScene.message.clear();
		Platform.runLater(()->gameScene.addMsg(chatMsg));
		Message msg = new Message();
		msg.chatMsg = chatMsg;
		clientConnection.send(msg);
	}

	private void createButtonHandlers(){
		gameScene.send.setOnAction(e->{
			chatMessage();
		});
		homeScene.header.settingsButton.setOnAction(e->{
			settingsScene.root.toFront();
		});
		settingsScene.header.leftButton.setOnAction(e->{
			homeScene.root.toFront();
		});
		lbScene.header.leftButton.setOnAction(e->{
			homeScene.root.toFront();
		});
		mmScene.header.leftButton.setOnAction(e->{
			homeScene.root.toFront();
			Message msg = new Message();
			msg.mm = "Cancel Match Making";
			System.out.println(msg.mm);
			clientConnection.send(msg);
		});
		gameScene.header.leftButton.setOnAction(e->{
			confirmff(multi);
		});
		homeScene.header.leftButton.setOnAction(e->{
			Platform.exit();
			System.exit(0);
		});
		homeScene.leaderboard.setOnAction(e->{
			lbSetup();
		});
		homeScene.findMatch.setOnAction(e->{
			multi = true;
			gameScene.send.setDisable(false);
			gameScene.message.setDisable(false);
			gameScene.chat.getItems().add("Game Chat");
			findMatch();
		});
		homeScene.playSolo.setOnAction(e->{
			multi = false;
			gameScene.send.setDisable(true);
			gameScene.message.setDisable(true);
			gameScene.board.resetBoard();
			gameboardInnit(true);
			gameScene.chat.getItems().clear();
			greg = new GregBot();
			greg.start();
			gameScene.root.toFront();
		});
		settingsScene.header.rightButton.setOnAction(e->{
			logout();
		});
		settingsScene.theme.setOnAction(e->{
			globalObjs.theme++;
			boolean dark;
			if(globalObjs.theme >= globalObjs.themes.size()){
				globalObjs.theme = 0;
				dark = false;
			} else {
                dark = true;
            }
            Platform.runLater(()-> {
				root.setStyle(globalObjs.themes.get(globalObjs.theme).getValue());
				settingsScene.theme.setText(globalObjs.themes.get(globalObjs.theme).getKey());
				homeScene.updateSceneImgs(dark);
				lbScene.updateSceneImgs(dark);
				settingsScene.updateSceneImgs(dark);
				gameScene.updateSceneImgs(dark);
				mmScene.updateSceneImgs(dark);
			});
		});
		lbScene.header.refresh.setOnAction(e->{
			lbScene.header.refresh.setDisable(true);
			Platform.runLater(()-> {
				lbScene.leaderboard.getItems().clear();
				lbSetup();
				Animations.greenFlash(lbScene.header.refresh);
			});
			lbScene.header.refresh.setDisable(false);
		});
	}

	private void confirmff(boolean multi){
		VBox popup;
		TextField popupLabel = new TextField("Surrender?");
		popupLabel.setEditable(false);
		popupLabel.getStyleClass().add("miniHeader");
		Button yes = new Button("Yes");
		yes.getStyleClass().add("yes-button");
		Button no = new Button("No");
		no.getStyleClass().add("no-button");
		HBox buttons = new HBox(10, no, yes);
		buttons.setAlignment(Pos.CENTER);
		popup = new VBox(10,popupLabel, buttons);
		popup.getStyleClass().add("elevatedBox");
		popup.setAlignment(Pos.CENTER);
		popup.setPadding(new Insets(15));
		popup.maxHeightProperty().bind(root.heightProperty().multiply(0.25));
		popup.maxWidthProperty().bind(root.widthProperty().multiply(0.6));
		no.maxWidthProperty().bind(popup.widthProperty().multiply(.3));
		yes.maxWidthProperty().bind(popup.widthProperty().multiply(.3));StackPane popupBg = new StackPane();
		popupBg.setAlignment(Pos.CENTER);
		popupBg.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
		root.getChildren().addAll(popup, popupBg);
		ffRemoverTool = new FFRemoverTool(popup, popupBg);
		no.setOnAction(event -> {
			ffRemoverTool.removeff();
			ffRemoverTool = null;
		});
		yes.setOnAction(event -> {
			ffRemoverTool.removeff();
			ffRemoverTool = null;
			if(multi) {
				Message msg = new Message();
				msg.matchResult = "FF";
				clientConnection.send(msg);
			}
			else{
				greg.stop();
				greg = null;
				homeScene.root.toFront();
				gameScene.board.resetBoard();
			}
		});
		popupBg.toFront();
		popup.toFront();
	}

	private class FFRemoverTool{
		VBox popup;
		StackPane popupBg;
		FFRemoverTool(VBox popup, StackPane popupBg){
			this.popup = popup;
			this.popupBg = popupBg;
		}
		private void removeff(){
			Platform.runLater(()->{
				root.getChildren().removeAll(popupBg, popup);
			});
		}
	}

	private void winLossPopup(int reason, boolean won, boolean single){
		greg = null;
		if(ffRemoverTool != null){
			ffRemoverTool.removeff();
			ffRemoverTool = null;
		}
		System.out.println("WinLossPopup");
		Thread thread = new Thread(() -> {
			VBox popup;
			HBox buttons;
			TextField popupLabel = new TextField();
			popupLabel.setEditable(false);
			popupLabel.getStyleClass().add("header");
			Button quit = new Button("Quit");
			Button rematch = new Button("Rematch");
			quit.getStyleClass().add("yes-button");
			rematch.getStyleClass().add("no-button");
			StackPane popupBg = new StackPane();
			popupBg.setAlignment(Pos.CENTER);
			popupBg.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
			rematch.prefHeightProperty().bind(quit.heightProperty());
			rematch.prefWidthProperty().bind(quit.widthProperty());
			quit.prefWidthProperty().bind(root.widthProperty().multiply(0.3));
			if (reason == 0) {
				// opponent dc'd
				popupLabel.setText("Disconnected");
				buttons = new HBox(quit);
				popup = new VBox(10, popupLabel, buttons);
			} else {
				// game ends normally/ff
				if(!single) {
					buttons = new HBox(10, rematch, quit);
				}
				else{
					buttons = new HBox(quit);
				}
				buttons.setAlignment(Pos.CENTER);
				popup = new VBox(10, popupLabel, buttons);
				if (won) {
					// you win
					popupLabel.setText("You Win!");
					popupLabel.setStyle("-fx-text-fill: green;");
				} else {
					// you lost
					if(reason == 1) {
						popupLabel.setText("You Lose!");
						popupLabel.setStyle("-fx-text-fill: red;");
					} else{
						popupLabel.setText("DRAW!");
						popupLabel.setStyle("-fx-text-fill: yellow;");
					}
				}
			}
			popup.setAlignment(Pos.CENTER);
			Platform.runLater(() -> root.getChildren().addAll(popup, popupBg));
			Thread listenForResponse = new Thread(() -> {
				System.out.println("Listening for quit response");
				while(true) {
					try {
						clientConnection.latch.await();
					} catch (InterruptedException e) {
						return;
					}
					System.out.println("Heard quit response");
					if(clientConnection.message.rematch != null || clientConnection.message.matchResult != null) {
						if ((Boolean.FALSE.equals(clientConnection.message.rematch) && clientConnection.message.rematch != null) || clientConnection.message.matchResult != null) {
							Platform.runLater(() -> {
								root.getChildren().removeAll(popup, popupBg);
								homeScene.root.toFront();
								System.out.println("Going home bc dc/dont want rematch");
							});
							break;
						}
					}
				}
			});

			quit.setOnAction(e -> {
				System.out.println("Quit");
				Platform.runLater(() -> {
					root.getChildren().removeAll(popup, popupBg);
					homeScene.root.toFront();
					listenForResponse.interrupt();
				});
				if(!single) {
					Message msg = new Message();
					msg.rematch = false;
					clientConnection.send(msg);
				}
			});
			rematch.setOnAction(e -> {
				Platform.runLater(() -> {
					popup.getChildren().removeAll(buttons);
					popupLabel.setText("Awaiting Opponent");
					popupLabel.setStyle("-fx-text-fill: -fontColor;");
					System.out.println("Removed buttons and changed text waiting for opp");
				});
				Thread thread2 = new Thread(() -> {
					Message msg = new Message();
					msg.rematch = true;
					clientConnection.send(msg);
					System.out.println("Sent message and awaiting response for rematch");
					try {
						clientConnection.latch.await();
					} catch (InterruptedException ex) {
						throw new RuntimeException(ex);
					}
					System.out.println("Got response for rematch");
					if(clientConnection.message.matchResult != null){
						Platform.runLater(() -> {
							root.getChildren().removeAll(popup, popupBg);
							homeScene.root.toFront();
							System.out.println("Removed all and went home");
						});
					}
					else if (!clientConnection.message.rematch) {
						Platform.runLater(() -> {
							root.getChildren().removeAll(popup, popupBg);
							homeScene.root.toFront();
							System.out.println("Removed all and went home");
						});
					} else {
						Platform.runLater(() -> {
							root.getChildren().removeAll(popup, popupBg);
							System.out.println("Removed all");
						});
						if(clientConnection.message.myTurn){
							turnLatch = new CountDownLatch(0);
						}
						else{
							turnLatch = new CountDownLatch(1);
						}
						System.out.println("Starting new match");
						listenForResponse.interrupt();
						Platform.runLater(()->{
							Thread godThisIsTerribleCode = new Thread(this::waitForResults);
							godThisIsTerribleCode.start();
						});
					}
				});
				thread2.start();
			});
			Platform.runLater(() -> {
				buttons.setAlignment(Pos.CENTER);
				popupBg.toFront();
				popup.toFront();
			});
			if(!single) {
				listenForResponse.start();
			}
		});
		thread.start();
	}

	private void createScenes(){
		homeScene = new HomeScene(globalObjs);
		settingsScene = new SettingsScene(globalObjs);
		lbScene = new LBScene(globalObjs);
		mmScene = new MMScene(globalObjs);
		gameScene = new GameScene(globalObjs);
		root = new StackPane(homeScene.root, settingsScene.root, lbScene.root, mmScene.root, gameScene.root);
	}

	private void logout(){
		Message msg = new Message();
		msg.logout = true;
		clientConnection.send(msg);
		globalObjs.account = null;
		loginAccount();
	}

	private class GregBot extends Thread {
		@Override
		public void run() {
			while(true) {
				if(turnLatch.getCount() == 0) {
					Platform.runLater(() -> {gameScene.header.header.setText("My Turn"); });
				}
				while(turnLatch.getCount() != 1);
				if(gameScene.board.checkWinner('1')){
					Platform.runLater(()->winLossPopup(1, true, true));
					return;
				}
				boolean winningMove = false;
				int moveCol = -1;
				Set<Integer> dontGo = new HashSet<>();
				// Find a move that wins the game
				for(int i = 0; i < 7; i++) {
					GameBoard testGameboard = new GameBoard(gameScene.board);
					if (gameScene.board.textBoard.get(i).get(0) == 'E'){
						if (testGameboard.playMove(i, '2')) {
							System.out.println("Found a winning move at " + i);
							moveCol = i;
							winningMove = true;
							break;
						}
					}
					else{
						System.out.println("Found a full col at " + i);
						dontGo.add(i);
					}
				}
				// Find a move that prevents player from winning
				if(moveCol == -1) {
					for(int i = 0; i < 7; i++) {
						GameBoard testGameboard = new GameBoard(gameScene.board);
						// Play a move
						if (gameScene.board.textBoard.get(i).get(0) == 'E') {
							testGameboard.playMove(i, '2');
							for (int j = 0; j < 7; j++) {
								// Player "plays" a move
								GameBoard testGameboard2 = new GameBoard(testGameboard);
								if (gameScene.board.textBoard.get(j).get(0) == 'E'){
									if (testGameboard2.playMove(j, '1')) { // Player can win the game
										// We block the winning move
										if (i != j) {
											System.out.println("Found a blocking move at " + j);
											moveCol = j;
											break;
										} else {
											// We do not go there
											System.out.println("Found a dumb move at " + j);
											dontGo.add(i);
										}
									}
								}
							}
						}
						else{
							System.out.println("Found a full col at " + i);
							dontGo.add(i);
						}
					}
				}
				// No blocking/winning moves found
				if(moveCol == -1) {
					// pick a random move with a bias towards the middle
					ArrayList<Integer> randomMoveList = new ArrayList<>();
					ArrayList<Integer> biasFactors = new ArrayList<>();
					biasFactors.add(0,1);
					biasFactors.add(1,2);
					biasFactors.add(2,3);
					biasFactors.add(3,4);
					biasFactors.add(4,3);
					biasFactors.add(5,2);
					biasFactors.add(6,1);
					for(int i = 0; i < 7; i++) {
						if(dontGo.contains(i)){
							continue;
						}
						for(int j = 0; j < biasFactors.get(i); j++) {
							randomMoveList.add(i);
						}
					}
					if(randomMoveList.isEmpty()){
						for(int i = 0; i < 7; i++) {
							if (gameScene.board.textBoard.get(i).get(0) == 'E') {
								moveCol = i;
							}
						}
					}
					else {
						System.out.println("random moves:\n"+randomMoveList);
						moveCol = randomMoveList.get((int) (Math.random() * randomMoveList.size()));
					}
				}
				gameScene.board.update(moveCol, '2');
				System.out.println("Playing move at " + moveCol);
				System.out.println("dontgoes:\n"+dontGo);
				if(winningMove) {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Platform.runLater(()->winLossPopup(1, false, true));
					return;
				}
				if(gameScene.board.moves >= 42){
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Platform.runLater(()->winLossPopup(-1, false, true));
					return;
				}
				turnLatch.countDown();
			}
		}
	}

	private void gameboardInnit(boolean single){
			gameScene.board.printBoard();
			turnLatch = new CountDownLatch(1);
			if(single){
				turnLatch.countDown();
			}
			else if(clientConnection.message.myTurn != null) {
				if (clientConnection.message.myTurn) {
					turnLatch.countDown();
				}
			}
			for(int i = 0; i < gameScene.board.vBoxes.size(); i++) {
				int finalI = i;
				gameScene.board.buttons.get(i).setOnAction(e -> {
					if (turnLatch.getCount() == 0) { // its my turn
						if (gameScene.board.textBoard.get(finalI).get(0) == 'E') {
							System.out.println("validMove");
							gameScene.board.update(finalI, '1');
							turnLatch = new CountDownLatch(1);
							if(!single) {
								Message msg = new Message();
								msg.move = finalI;
								clientConnection.send(msg);
							}
							gameScene.header.header.setText("Waiting");
						}
					}
				});
			}
	}

	private void waitForResults(){
		gameScene.board.resetBoard();
		Platform.runLater(()->{gameScene.clearChatbox();});
		if(turnLatch.getCount() == 0) {
			gameScene.header.header.setText("My Turn");
		} else {
			gameScene.header.header.setText("Waiting");
		}
		while(true) {
			try {
				System.out.println("Waiting for results...");
				clientConnection.latch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			if(clientConnection.message.myTurn != null) {
				if (clientConnection.message.myTurn) {
					turnLatch.countDown();
				}
			}
			if(clientConnection.message.chatMsg != null) {
				Platform.runLater(()->gameScene.addMsg(clientConnection.message.chatMsg));
			}
			if(turnLatch.getCount() == 0) {
				Platform.runLater(() -> {gameScene.header.header.setText("My Turn"); });
			}
			if(clientConnection.message.move != null){
				gameScene.board.update(clientConnection.message.move, '2');
			}
			if(clientConnection.message.matchResult != null) {
				System.out.println("Got Match results");
				System.out.println(clientConnection.message.matchResult);
				if (Objects.equals(clientConnection.message.matchResult, "DC 1")) {
					winLossPopup(0, true, false);
					break;
				} else if (Objects.equals(clientConnection.message.matchResult, "0")) {
					winLossPopup(1, false, false);
					break;
				} else if (Objects.equals(clientConnection.message.matchResult, "1")) {
					winLossPopup(1, true, false);
					break;
				} else if (Objects.equals(clientConnection.message.matchResult, "-1")) {
					winLossPopup(-1, false, false);
					break;
				}
			}
		}
	}

	private void findMatch(){
		mmScene.root.toFront();
		mmScene.startStatusEllipses();
		Thread thread = new Thread(() -> {
            Message msg = new Message();
            msg.mm = "Find Match";
			System.out.println(msg.mm);
            clientConnection.send(msg);
            try {
                clientConnection.latch.await();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            if(Objects.equals(clientConnection.message.mm, "Match Found")){
				mmScene.killStatusEllipses();
				System.out.println(clientConnection.message.mm);
				Platform.runLater(()-> gameScene.root.toFront());
				// start a thread to handle the game
				// Now we wait for the match to conclude
				gameboardInnit(false);
               	waitForResults();
			}
        });
		thread.start();
	}

	private void lbSetup(){
		Message message = new Message();
		message.lbRequest = true;
		clientConnection.send(message);
		try {
			clientConnection.latch.await();
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
		try {
			lbScene.loadLeaderboard(clientConnection.message.lbData);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		lbScene.root.toFront();
	}

	private void loginAccount(){
		System.out.println("Login Account");
		VBox popup;
		TextField popupLabel = new TextField("Login/Register");
		popupLabel.setEditable(false);
		popupLabel.getStyleClass().add("miniHeader");
		TextField username = new TextField();
		TextField password = new TextField();
		TextField feedback = new TextField();
		username.setPromptText("Username");
		username.getStyleClass().add("inputTxt");
		password.setPromptText("Password");
		password.getStyleClass().add("inputTxt");
		feedback.setEditable(false);
		Button submit = new Button("Submit");
		popup = new VBox(10,popupLabel,username, password, feedback,submit);
		popup.getStyleClass().add("elevatedBox");
		popup.setAlignment(Pos.CENTER);
		popup.setPadding(new Insets(15));
		popup.maxHeightProperty().bind(root.heightProperty().multiply(0.25));
		popup.maxWidthProperty().bind(root.widthProperty().multiply(0.5));
		StackPane popupBg = new StackPane();
		popupBg.setAlignment(Pos.CENTER);
		popupBg.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
		root.getChildren().addAll(popup, popupBg);
		submit.setOnAction(event -> {
			submit.setDisable(true);
			if(!username.getText().isEmpty() && !password.getText().isEmpty()) {
				Message message = new Message();
				message.account = new Message.Account();
				message.account.username = username.getText();
				message.account.password = password.getText();
				clientConnection.send(message);
				try {
					clientConnection.latch.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				String loginFeedback = clientConnection.message.loginFeedback;
				if(loginFeedback.contains("ERR")){
					username.clear();
					password.clear();
					feedback.setStyle("-fx-text-fill: red;");
					if(loginFeedback.contains("Invalid")) {
						feedback.setText("Invalid Password");
					} else if (loginFeedback.contains("in use")) {
						feedback.setText("Account is in use");
					}
					submit.setDisable(false);
				}
				else {
					TextField success = new TextField();
					success.setEditable(false);
					if(loginFeedback.contains("registered")){
						success.setText("Successfully Registered");
					}
					else{
						success.setText("Successfully Logged in");
					}
					success.getStyleClass().add("header");
					success.setAlignment(Pos.CENTER);
					success.setStyle("-fx-text-fill: green;");
					root.getChildren().add(success);
					success.toFront();
					globalObjs.account = clientConnection.message.account;
					homeScene.user.setText(username.getText());
					Animations.fade(List.of(popup, popupBg, success), root);
				}
			}
			else{
				username.clear();
				password.clear();
				feedback.setText("Please fill all the fields");
				feedback.setStyle("-fx-text-fill: red;");
				submit.setDisable(false);
			}
		});
		popupBg.toFront();
		popup.toFront();
	}
}
