package application;

import javafx.application.Application;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main extends Application {

    static class Pokemon<T> {
    	String image;
        private String name;
        private String type;
        private int health;
        private int attack;
        private int defense;
        private int speed;
        private int specialAttack;
        private int specialDefense;
        private int evasiveness;
        private List<Move> moves;

        public Pokemon(String imagelink, String name, String type, int health, int attack, int defense, int speed, int specialAttack, int specialDefense, int evasiveness, List<Move> moves) {
            image = imagelink;
        	this.name = name;
            this.type = type;
            this.health = health;
            this.attack = attack;
            this.defense = defense;
            this.speed = speed;
            this.specialAttack = specialAttack;
            this.specialDefense = specialDefense;
            this.evasiveness = evasiveness;
            this.moves = moves;
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public int getHealth() { return health; }
        public int getAttack() { return attack; }
        public int getDefense() { return defense; }
        public int getSpeed() { return speed; }
        public int getSpecialAttack() { return specialAttack; }
        public int getSpecialDefense() { return specialDefense; }
        public int getEvasiveness() { return evasiveness; }
        public List<Move> getMoves() { return moves; }

        public synchronized void reduceHealth(int amount) {
            health -= amount;
            if (health < 0) {
                health = 0;
            }
        }

        public synchronized void increaseStat(String stat, int amount) {
            switch (stat) {
                case "attack":
                    attack += amount;
                    break;
                case "defense":
                    defense += amount;
                    break;
                case "speed":
                    speed += amount;
                    break;
                case "specialAttack":
                    specialAttack += amount;
                    break;
                case "specialDefense":
                    specialDefense += amount;
                    break;
                case "evasiveness":
                    evasiveness += amount;
                    break;
            }
        }

        public boolean isFainted() {
            return health <= 0;
        }

        @Override
        public String toString() {
            return name + " (" + type + ")";
        }

 
        public String getStats() {
            return String.format("Type: %s\nHP: %d\nAttack: %d\nDefense: %d\nSpeed: %d\nSpecial Attack: %d\nSpecial Defense: %d\nEvasiveness: %d", type, health, attack, defense, speed, specialAttack, specialDefense, evasiveness);
        }
    }

  
    static class Move {
        private String name;
        private int damage;
        private String effect;  
        private String stat;    
        private int effectAmount;
        private String moveType; 

        public Move(String name, int damage, String effect, String stat, int effectAmount, String moveType) {
            this.name = name;
            this.damage = damage;
            this.effect = effect;
            this.stat = stat;
            this.effectAmount = effectAmount;
            this.moveType = moveType;
        }

        public String getName() { return name; }
        public int getDamage() { return damage; }
        public String getEffect() { return effect; }
        public String getStat() { return stat; }
        public int getEffectAmount() { return effectAmount; }
        public String getMoveType() { return moveType; }
    }
    private String getEffectiveness(String attackType, String opponentType) {
        switch (attackType) {
            case "Fire":
                return opponentType.equals("Grass") ? "Super Effective" : "Not Very Effective";
            case "Water":
                return opponentType.equals("Fire") ? "Super Effective" : "Not Very Effective";
            case "Electric":
                return opponentType.equals("Water") ? "Super Effective" : "Not Very Effective";
            case "Grass":
                return opponentType.equals("Water") ? "Super Effective" : "Not Very Effective";
            default:
                return "Effective";
        }
    }

    static class Item {
        private String name;
        private int healAmount;
        private String effect;

        public Item(String name, int healAmount, String effect) {
            this.name = name;
            this.healAmount = healAmount;
            this.effect = effect;
        }

        public String getName() { return name; }
        public int getHealAmount() { return healAmount; }
        public String getEffect() { return effect; }
    }

    static class InvalidMoveException extends Exception {
        public InvalidMoveException(String message) { super(message); }
    }
   
    static class Battle {
        private Pokemon<?> playerPokemon;
        private Pokemon<?> opponentPokemon;
        private Item potion;

        public Battle(Pokemon<?> playerPokemon, Pokemon<?> opponentPokemon) {
            this.playerPokemon = playerPokemon;
            this.opponentPokemon = opponentPokemon;
            this.potion = new Item("Potion", 20, "heal"); // Example item
        }
        void gameOver(String msg) {
        	Alert alert = new Alert(AlertType.CONFIRMATION);
        	alert.setTitle("POKEMON");
        	alert.setHeaderText(null);
        	alert.setContentText(msg);
        	
        	
        	
        	ImageView imageView = new ImageView(new Image("https://w7.pngwing.com/pngs/337/240/png-transparent-pokeball-pokemon-battle-revolution-pikachu-entei-pokeball-sphere-pokemon-bulbasaur-thumbnail.png"));
        	imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            alert.getDialogPane().setGraphic(imageView);
            
        	alert.showAndWait().ifPresent(response -> {
        		if(response == ButtonType.OK || response == ButtonType.CANCEL) {
        			Platform.exit();
        		}
        	});
        }

        public synchronized void playerAttack(Move move, ProgressBar opponentHealthBar, Label opponentLabel) throws InvalidMoveException {
            if (playerPokemon.isFainted()) {
            	System.out.println("Your Pokémon has fainted!");
            	gameOver("You lost!!!");
            }

            if (move.getEffect().equals("damage")) {
                opponentPokemon.reduceHealth(move.getDamage());
            } else if (move.getEffect().equals("boost")) {
                playerPokemon.increaseStat(move.getStat(), move.getEffectAmount());
            } else if (move.getEffect().equals("decrease")) {
                opponentPokemon.increaseStat(move.getStat(), -move.getEffectAmount());
            }

            updateHealthBar(opponentPokemon, opponentHealthBar, opponentLabel);
            System.out.println( opponentPokemon + " used "+move.getName());

            if (opponentPokemon.isFainted()) {
                System.out.println("Opponent's Pokémon has fainted!");
                gameOver("You won!!!");
            }
        }

        public synchronized void opponentAttack(Move move, ProgressBar playerHealthBar, Label playerLabel) {
            if (move.getEffect().equals("damage")) {
                playerPokemon.reduceHealth(move.getDamage());
            } else if (move.getEffect().equals("boost")) {
                opponentPokemon.increaseStat(move.getStat(), move.getEffectAmount());
            } else if (move.getEffect().equals("decrease")) {
                playerPokemon.increaseStat(move.getStat(), -move.getEffectAmount());
            }

            updateHealthBar(playerPokemon, playerHealthBar, playerLabel);
            System.out.println( playerPokemon + " used "+move.getName());

            if (playerPokemon.isFainted()) {
                System.out.println("Your Pokémon has fainted!");
            }
        }

        public synchronized void useItem(Item item, ProgressBar playerHealthBar, Label playerLabel) {
            if (item.getEffect().equals("heal")) {
                playerPokemon.reduceHealth(-item.getHealAmount());
                updateHealthBar(playerPokemon, playerHealthBar, playerLabel);
            }
        }

        private void updateHealthBar(Pokemon<?> pokemon, ProgressBar healthBar, Label label) {
            Platform.runLater(() -> {
                healthBar.setProgress(pokemon.getHealth() / 100.0);
                label.setText(pokemon.getName() + " HP: " + pokemon.getHealth());
            });
        }
    }

    private Battle battle;
    private Pokemon<?> playerPokemon;
    private Pokemon<?> opponentPokemon;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Pokemon Battle Simulator");
        List<Pokemon<?>> pokemonList = Arrays.asList(
                new Pokemon<>("https://www.pngplay.com/wp-content/uploads/10/Charmander-Pokemon-PNG-Images-HD.png", "Charmander", "Fire", 100, 52, 43, 65, 60, 50, 50, Arrays.asList(
                        new Move("Ember", 15, "damage", "", 0, "Fire"),
                        new Move("Scratch", 10, "damage", "", 0, "Normal"),
                        new Move("Growl", 0, "boost", "attack", 2, "Normal"),
                        new Move("Flamethrower", 35, "damage", "", 0, "Fire"))),
                new Pokemon<>("https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/c02cdf55-057c-493a-9467-7fbcc417064b/dftv317-a1d4ce3d-76b7-443a-9bf0-4d7509cc0944.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7InBhdGgiOiJcL2ZcL2MwMmNkZjU1LTA1N2MtNDkzYS05NDY3LTdmYmNjNDE3MDY0YlwvZGZ0djMxNy1hMWQ0Y2UzZC03NmI3LTQ0M2EtOWJmMC00ZDc1MDljYzA5NDQucG5nIn1dXSwiYXVkIjpbInVybjpzZXJ2aWNlOmZpbGUuZG93bmxvYWQiXX0.pQndAkjndTNqxcBkS3OCYIp61W25sCS_1wGJn96CxLY", "Squirtle", "Water", 100, 48, 65, 43, 50, 44, 50, Arrays.asList(
                        new Move("Water Gun", 12, "damage", "", 0, "Water"),
                        new Move("Tackle", 7, "damage", "", 0, "Normal"),
                        new Move("Bubble", 10, "damage", "", 0, "Water"),
                        new Move("Hydro Pump", 40, "damage", "", 0, "Water"))),
                new Pokemon<>("https://th.bing.com/th/id/R.53d5cb549ceec029269503de59d9ae91?rik=UlTs6FtZoUxktg&riu=http%3a%2f%2forig08.deviantart.net%2f7b9e%2ff%2f2016%2f108%2f4%2f2%2fbulbasaur_png_by_kriss116-d9zfl1f.png&ehk=1qeEZFyMtBPvKnLPYktaj7PwL7bcPNcAcNxEXSaLkKU%3d&risl=&pid=ImgRaw&r=0", "Bulbasaur", "Grass", 100, 49, 49, 45, 65, 49, 65, Arrays.asList(
                        new Move("Vine Whip", 13, "damage", "", 0, "Grass"),
                        new Move("Tackle", 5, "damage", "", 0, "Normal"),
                        new Move("Razor Leaf", 10, "damage", "", 0, "Grass"),
                        new Move("Swords Dance", 0, "boost", "attack", 2, "Normal"))),
                new Pokemon<>("https://www.pngmart.com/files/2/Pikachu-Transparent-Background.png", "Pikachu", "Electric", 100, 55, 40, 90, 50, 50, 60, Arrays.asList(
                        new Move("Thunderbolt", 20, "damage", "", 0, "Electric"),
                        new Move("Quick Attack", 5, "damage", "", 0, "Normal"),
                        new Move("Electro Ball", 15, "damage", "", 0, "Electric"),
                        new Move("Thunder Shock", 12, "damage", "", 0, "Electric"))),
                new Pokemon<>("https://vignette.wikia.nocookie.net/central/images/b/b1/Jigglypuff_by_cansin13art-d8pasot.png/revision/latest?cb=20170719021429", "Jigglypuff", "Normal", 100, 45, 20, 50, 45, 25, 35, Arrays.asList(
                        new Move("Pound", 9, "damage", "", 0, "Normal"),
                        new Move("Body Slam", 10, "damage", "", 0, "Normal"),
                        new Move("Double Slap", 6, "damage", "", 0, "Normal"),
                        new Move("Hyper Voice", 14, "damage", "", 0, "Normal"))),
                new Pokemon<>("https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/575ec21b-072c-4dc2-9964-69874e4837a3/d2ljbl1-968659c4-a4bf-40ba-b791-80cfaaeb1bcc.png/v1/fill/w_804,h_994,strp/eevee_redraw_by_kirkbutler_d2ljbl1-pre.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9MTAwMCIsInBhdGgiOiJcL2ZcLzU3NWVjMjFiLTA3MmMtNGRjMi05OTY0LTY5ODc0ZTQ4MzdhM1wvZDJsamJsMS05Njg2NTljNC1hNGJmLTQwYmEtYjc5MS04MGNmYWFlYjFiY2MucG5nIiwid2lkdGgiOiI8PTgwOSJ9XV0sImF1ZCI6WyJ1cm46c2VydmljZTppbWFnZS5vcGVyYXRpb25zIl19.jWgetIt1sRiA1wEBXZvQSb6mAhrsfoJK2_T74mpSSq4", "Eevee", "Normal", 100, 55, 50, 55, 50, 50, 55, Arrays.asList(
                        new Move("Bite", 12, "damage", "", 0, "Dark"),
                        new Move("Tackle", 7, "damage", "", 0, "Normal"),
                        new Move("Swift", 10, "damage", "", 0, "Normal"),
                        new Move("Take Down", 14, "damage", "", 0, "Normal"))),
                new Pokemon<>("https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/d42ea46a-328e-41a7-a1f2-0ef4c7c9c02e/d8urx41-53ff7c04-33da-474c-a6cb-cbfd311eab5a.png/v1/fill/w_832,h_960,strp/meowth_by_kol98_d8urx41-pre.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9MTE4MiIsInBhdGgiOiJcL2ZcL2Q0MmVhNDZhLTMyOGUtNDFhNy1hMWYyLTBlZjRjN2M5YzAyZVwvZDh1cng0MS01M2ZmN2MwNC0zM2RhLTQ3NGMtYTZjYi1jYmZkMzExZWFiNWEucG5nIiwid2lkdGgiOiI8PTEwMjQifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.bcAZZlBsYwXc1PS8ZYbYT1hy1G0XR-32g0fzDlh8pJM", "Meowth", "Normal", 100, 45, 35, 90, 40, 40, 80, Arrays.asList(
                        new Move("Scratch", 10, "damage", "", 0, "Normal"),
                        new Move("Bite", 9, "damage", "", 0, "Dark"),
                        new Move("Slash", 12, "damage", "", 0, "Normal"),
                        new Move("Fury Swipes", 11, "damage", "", 0, "Normal"))),
                new Pokemon<>("https://assets.pokemon.com/assets/cms2/img/pokedex/full/066.png", "Machop", "Fighting", 100, 80, 50, 35, 35, 35, 35, Arrays.asList(
                        new Move("Karate Chop", 15, "damage", "", 0, "Fighting"),
                        new Move("Low Kick", 8, "damage", "", 0, "Fighting"),
                        new Move("Seismic Toss", 10, "damage", "", 0, "Fighting"),
                        new Move("Cross Chop", 18, "damage", "", 0, "Fighting"))),
                new Pokemon<>("https://orig00.deviantart.net/277c/f/2016/201/d/8/gastly_by_linkniak-daapt5n.png", "Gastly", "Ghost", 100, 50, 30, 80, 60, 35, 45, Arrays.asList(
                        new Move("Lick", 10, "damage", "", 0, "Ghost"),
                        new Move("Night Shade", 12, "damage", "", 0, "Ghost"),
                        new Move("Shadow Ball", 18, "damage", "", 0, "Ghost"),
                        new Move("Dark Pulse", 15, "damage", "", 0, "Dark"))),
                new Pokemon<>("https://www.pngmart.com/files/22/Psyduck-Pokemon-PNG-Isolated-HD.png", "Psyduck", "Water", 100, 52, 48, 55, 65, 50, 55, Arrays.asList(
                        new Move("Water Gun", 12, "damage", "", 0, "Water"),
                        new Move("Zen Headbutt", 13, "damage", "", 0, "Psychic"),
                        new Move("Confusion", 10, "damage", "", 0, "Psychic"),
                        new Move("Aqua Tail", 15, "damage", "", 0, "Water")))
        );
        playerPokemon = promptPokemonSelection(pokemonList, "Select your Pokémon");
        opponentPokemon = pokemonList.get(new Random().nextInt(pokemonList.size()));
        battle = new Battle(playerPokemon, opponentPokemon);

        setupBattleUI(primaryStage);
    }

    private Pokemon<?> promptPokemonSelection(List<Pokemon<?>> pokemonList, String title) {
        ChoiceDialog<Pokemon<?>> dialog = new ChoiceDialog<>(pokemonList.get(0), pokemonList);
        dialog.setTitle(title);
        dialog.setHeaderText("Choose your Pokémon");
        dialog.setContentText("Select a Pokémon:");
        
        ImageView imageView = new ImageView(new Image("https://w7.pngwing.com/pngs/337/240/png-transparent-pokeball-pokemon-battle-revolution-pikachu-entei-pokeball-sphere-pokemon-bulbasaur-thumbnail.png"));
    	imageView.setFitHeight(48);
        imageView.setFitWidth(48);
        dialog.getDialogPane().setGraphic(imageView);
        
        dialog.showAndWait();
        return dialog.getSelectedItem();
    }

    private void setupBattleUI(Stage primaryStage) {
        BorderPane root = new BorderPane();
        

        String backgroundUrl = "https://cdn.vox-cdn.com/thumbor/Tw2oMPq6QzfFPQU0LK_DJ7AXZfU=/1400x788/filters:format(jpeg)/cdn.vox-cdn.com/uploads/chorus_asset/file/19376313/2019111121303700_3C66B776DB1AA06323037049FACD96D3.jpg";
        BackgroundImage backgroundImage = new BackgroundImage(
            new Image(backgroundUrl, 500, 500, false, true),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            BackgroundSize.DEFAULT
            
        );
        
        root.setBackground(new Background(backgroundImage));
      
        HBox opponentBox = new HBox(10);
        Label opponentLabel = new Label(opponentPokemon.getName() + " HP: " + opponentPokemon.getHealth());
        opponentLabel.setStyle("-fx-font-size:10px; -fx-text-fill: white; -fx-font-weight: bold");
        ProgressBar opponentHealthBar = new ProgressBar(1.0);
        ImageView opponentImage = new ImageView(new Image(opponentPokemon.image));
        
        opponentImage.setFitWidth(150);
        opponentImage.setFitHeight(150);
        opponentLabel.setTooltip(new Tooltip(opponentPokemon.getStats()));

        opponentBox.getChildren().addAll(opponentLabel, opponentHealthBar, opponentImage);
        opponentBox.setAlignment(Pos.CENTER);
        opponentBox.setPadding(new Insets(10));

        HBox playerBox = new HBox(10);
        Label playerLabel = new Label(playerPokemon.getName() + " HP: " + playerPokemon.getHealth());
        ProgressBar playerHealthBar = new ProgressBar(1.0);
        ImageView playerImage = new ImageView(new Image(playerPokemon.image));
        
        playerImage.setFitWidth(150);
        playerImage.setFitHeight(150);
        playerLabel.setTooltip(new Tooltip(playerPokemon.getStats()));
        playerLabel.setStyle("-fx-font-size:10px; -fx-text-fill: white; -fx-font-weight: bold");

        playerBox.getChildren().addAll(playerLabel, playerHealthBar, playerImage);
        playerBox.setAlignment(Pos.CENTER);
        playerBox.setPadding(new Insets(10));

        HBox movesBox = new HBox(10);
        movesBox.setAlignment(Pos.CENTER);
        movesBox.setPadding(new Insets(10));

        for (Move move : playerPokemon.getMoves()) {
            Button attackButton = new Button(move.getName());
            attackButton.setStyle("-fx-font-weight : bold; -fx-background-color : #ADD8E6; -fx-border-width:1; -fx-border-color: black");
            attackButton.setOnAction(event -> {
            	try {
                    battle.playerAttack(move, opponentHealthBar, opponentLabel);
                    Move opponentMove = opponentPokemon.getMoves().get(new Random().nextInt(opponentPokemon.getMoves().size()));
                    battle.opponentAttack(opponentMove, playerHealthBar, playerLabel);
                } catch (InvalidMoveException e) {
                    System.out.println(e.getMessage());
                }
            });
            String effectiveness = getEffectiveness(move.getMoveType(), opponentPokemon.getType());
            attackButton.setTooltip(new Tooltip("Effectiveness: " + effectiveness));

            movesBox.getChildren().add(attackButton);
        }

        Button potionButton = new Button("Use Potion");
        potionButton.setStyle("-fx-font-weight : bold; -fx-background-color : #CBC3E3; -fx-border-width:1; -fx-border-color: black");
        potionButton.setOnAction(event -> {
            battle.useItem(new Item("Potion", 20, "heal"), playerHealthBar, playerLabel);
        });
        movesBox.getChildren().add(potionButton);

        root.setTop(opponentBox);
        root.setCenter(playerBox);
        root.setBottom(movesBox);

        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
