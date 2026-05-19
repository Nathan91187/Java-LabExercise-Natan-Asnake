package Game;

import data.Card;
import data.HandRank;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

import static javafx.application.Application.launch;

public class Poker extends Application {

    private List<Card> deck;
    private List<Card> playerHand;
    private List<Card> dealerHand;

    private Label playerLbl;
    private Label dealerLbl;
    private Label resultLbl;

    private int playerChips = 100;
    private int dealerChips = 100;
    private int pot = 0;
    private int Bet = 10;

    private Label chipLbl;

    @Override
    public void start(Stage stage) {
        playerLbl = new Label("Player Hand: ");
        dealerLbl = new Label("Dealer Hand: ");
        resultLbl = new Label("Press DEAL to start");
        chipLbl = new Label();
        Button dealBtn = new Button("DEAL");
        Button callBtn = new Button("CALL");
        Button raiseBtn = new Button("RAISE +10");
        Button foldBtn = new Button("FOLD");
        dealBtn.setOnAction(e -> play());
        callBtn.setOnAction(e -> {
            playerChips -= Bet;
            dealerChips -= Bet;
            pot += Bet * 2;
            updateChips();
            resultLbl.setText("Both players called. Pot = " + pot);
        });

        raiseBtn.setOnAction(e -> {
            Bet += 10;
            resultLbl.setText("Bet raised to " + Bet);
        });
        foldBtn.setOnAction(e -> {
            dealerChips += pot;
            pot = 0;
            updateChips();
            resultLbl.setText("Player folded. Dealer wins pot.");
        });
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                playerLbl,
                dealerLbl,
                chipLbl,
                dealBtn,
                callBtn,
                raiseBtn,
                foldBtn,
                resultLbl
        );
        Scene scene = new Scene(root, 500, 250);
        stage.setTitle("Poker Game");
        stage.setScene(scene);
        updateChips();
        stage.show();
    }
    private void play() {
        createDeck();
        shuffle();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            playerHand.add(deck.remove(0));
            dealerHand.add(deck.remove(0));
        }
        playerLbl.setText("Player Hand: " + playerHand);
        dealerLbl.setText("Dealer Hand: " + dealerHand);
        HandRank playerRank = evaluateHand(playerHand);
        HandRank dealerRank = evaluateHand(dealerHand);
        String result;
        if (playerRank.value > dealerRank.value) {
            result = "Player Wins with " + playerRank.name;
        } else if (dealerRank.value > playerRank.value) {
            result = "Dealer Wins with " + dealerRank.name;
        } else {
            result = "Tie! Both have " + playerRank.name;
        }
        if (playerRank.value > dealerRank.value) {
            playerChips += pot;
        } else if (dealerRank.value > playerRank.value) {
            dealerChips += pot;
        } else {
            playerChips += pot / 2;
            dealerChips += pot / 2;
        }
        pot = 0;
        updateChips();
        resultLbl.setText(result);
    }
    private void createDeck() {
        deck = new ArrayList<>();
        String[] suits = {"♠", "♥", "♦", "♣"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(new Card(rank, suit));
            }
        }
    }
    private void shuffle() {
        Collections.shuffle(deck);
    }
    private HandRank evaluateHand(List<Card> hand) {
        Map<Integer, Integer> counts = new HashMap<>();
        List<Integer> values = new ArrayList<>();
        for (Card card : hand) {
            int value = card.getValue();
            values.add(value);
            counts.put(value, counts.getOrDefault(value, 0) + 1);
        }
        Collections.sort(values);
        boolean flush = isFlush(hand);
        boolean straight = isStraight(values);
        Collection<Integer> freq = counts.values();
        if (straight && flush && values.get(4) == 14) {
            return new HandRank("Royal Flush", 10);
        }
        if (straight && flush) {
            return new HandRank("Straight Flush", 9);
        }
        if (freq.contains(4)) {
            return new HandRank("Four of a Kind", 8);
        }
        if (freq.contains(3) && freq.contains(2)) {
            return new HandRank("Full House", 7);
        }
        if (flush) {
            return new HandRank("Flush", 6);
        }
        if (straight) {
            return new HandRank("Straight", 5);
        }
        if (freq.contains(3)) {
            return new HandRank("Three of a Kind", 4);
        }
        int pairCount = 0;
        for (int c : freq) {
            if (c == 2) pairCount++;
        }
        if (pairCount == 2) {
            return new HandRank("Two Pair", 3);
        }
        if (pairCount == 1) {
            return new HandRank("One Pair", 2);
        }
        return new HandRank("High Card", 1);
    }
    private void updateChips() {
        chipLbl.setText(
                "Player Chips: " + playerChips +
                        " | Dealer Chips: " + dealerChips +
                        " | Pot: " + pot +
                        " | Current Bet: " + Bet
        );
    }
    private boolean isFlush(List<Card> hand) {
        String suit = hand.get(0).suit;
        for (Card card : hand) {
            if (!card.suit.equals(suit)) {
                return false;
            }
        }
        return true;
    }
    private boolean isStraight(List<Integer> values) {
        for (int i = 0; i < values.size() - 1; i++) {
            if (values.get(i + 1) != values.get(i) + 1) {
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args) {
        launch(args);
    }
}

