package TwoPlayerMathQuiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class TwoPlayerMathQuizGUI extends JFrame {

    private Random rand = new Random();
    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;
    private final int MAX_TIME = 10;
    private int totalRounds;
    private int currentRound = 1;

    private JLabel labelRound;
    private JLabel labelQuestion;
    private JProgressBar progressBarTimer;
    private JLabel labelPlayer1;
    private JLabel labelPlayer2;
    private JTextField textAnswer;
    private JButton buttonSubmit;

    private String currentPlayer = "Hercules";
    private int expectedAnswer;
    private AtomicBoolean isAnswered = new AtomicBoolean(false);
    private Timer timer;

    public TwoPlayerMathQuizGUI() {
        setTitle("Two Player Math Quiz Game");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        // Set background image
        JLabel background = new JLabel(new ImageIcon("background.jpg"));
        background.setBounds(0, 0, 500, 400);
        add(background);

        // Round Label
        labelRound = new JLabel("Round 1");
        labelRound.setFont(new Font("Arial", Font.BOLD, 20));
        labelRound.setForeground(Color.WHITE);
        labelRound.setBounds(200, 20, 150, 30);
        background.add(labelRound);

        // Question Label
        labelQuestion = new JLabel();
        labelQuestion.setFont(new Font("Arial", Font.BOLD, 18));
        labelQuestion.setForeground(Color.YELLOW);
        labelQuestion.setHorizontalAlignment(SwingConstants.CENTER);
        labelQuestion.setBounds(50, 80, 400, 30);
        background.add(labelQuestion);

        // Timer ProgressBar
        progressBarTimer = new JProgressBar(0, MAX_TIME);
        progressBarTimer.setForeground(Color.GREEN);
        progressBarTimer.setBounds(150, 120, 200, 30);
        background.add(progressBarTimer);

        // Player 1 Label
        labelPlayer1 = new JLabel("Hercules: 0");
        labelPlayer1.setFont(new Font("Arial", Font.BOLD, 16));
        labelPlayer1.setForeground(Color.CYAN);
        labelPlayer1.setBounds(30, 170, 200, 30);
        background.add(labelPlayer1);

        // Player 2 Label
        labelPlayer2 = new JLabel("Thor: 0");
        labelPlayer2.setFont(new Font("Arial", Font.BOLD, 16));
        labelPlayer2.setForeground(Color.CYAN);
        labelPlayer2.setBounds(320, 170, 200, 30);
        background.add(labelPlayer2);

        // Answer TextField
        textAnswer = new JTextField();
        textAnswer.setFont(new Font("Arial", Font.PLAIN, 16));
        textAnswer.setBounds(150, 220, 200, 30);
        background.add(textAnswer);

        // Submit Button
        buttonSubmit = new JButton("Submit");
        buttonSubmit.setFont(new Font("Arial", Font.BOLD, 16));
        buttonSubmit.setBackground(Color.ORANGE);
        buttonSubmit.setBounds(200, 270, 100, 40);
        background.add(buttonSubmit);

        buttonSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitAnswer();
            }
        });

        startGame();
    }

    private void startGame() {
        totalRounds = Integer.parseInt(JOptionPane.showInputDialog(this, "How many rounds would you like to play?", "2"));

        presentQuestion();
    }

    private void presentQuestion() {
        isAnswered.set(false);
        textAnswer.setText("");
        progressBarTimer.setValue(MAX_TIME);
        timer = new Timer();

        int number1 = rand.nextInt(20) + 1;
        int number2 = rand.nextInt(20) + 1;
        char mathOperator = getRandomOperator();

        expectedAnswer = calculate(number1, number2, mathOperator);

        labelQuestion.setText(currentPlayer + ", solve this: " + number1 + " " + mathOperator + " " + number2 + " = ?");
        labelRound.setText("Round " + currentRound);

        timer.scheduleAtFixedRate(new TimerTask() {
            int timeRemaining = MAX_TIME;

            @Override
            public void run() {
                if (isAnswered.get()) {
                    timer.cancel();
                } else if (timeRemaining <= 0) {
                    timer.cancel();
                    timeIsUp();
                } else {
                    progressBarTimer.setValue(--timeRemaining);
                }
            }
        }, 1000, 1000);
    }

    private void submitAnswer() {
        if (!isAnswered.get()) {
            isAnswered.set(true);
            int playerResponse;
            try {
                playerResponse = Integer.parseInt(textAnswer.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
                isAnswered.set(false);
                return;
            }

            if (playerResponse == expectedAnswer) {
                JOptionPane.showMessageDialog(this, "Correct!");
                if (currentPlayer.equals("Hercules")) {
                    scorePlayer1++;
                    labelPlayer1.setText("Hercules: " + scorePlayer1);
                } else {
                    scorePlayer2++;
                    labelPlayer2.setText("Thor: " + scorePlayer2);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Wrong! The correct answer was: " + expectedAnswer);
            }

            nextRound();
        }
    }

    private void timeIsUp() {
        JOptionPane.showMessageDialog(this, "Time is up! The correct answer was: " + expectedAnswer);
        nextRound();
    }

    private void nextRound() {
        currentPlayer = currentPlayer.equals("Hercules") ? "Thor" : "Hercules";
        if (currentPlayer.equals("Hercules")) {
            currentRound++;
        }
        if (currentRound <= totalRounds) {
            presentQuestion();
        } else {
            endGame();
        }
    }

    private void endGame() {
        String winner;
        if (scorePlayer1 > scorePlayer2) {
            winner = "Hercules is the winner!";
        } else if (scorePlayer2 > scorePlayer1) {
            winner = "Thor is the winner!";
        } else {
            winner = "It's a tie!";
        }

        JOptionPane.showMessageDialog(this, "Final Scores:\nHercules: " + scorePlayer1 + "\nThor: " + scorePlayer2 + "\n" + winner);
        System.exit(0);
    }

    private char getRandomOperator() {
        char[] operators = {'+', '-', '*', '/'};
        return operators[rand.nextInt(operators.length)];
    }

    private int calculate(int a, int b, char operator) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                return a / b;
            default:
                return 0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TwoPlayerMathQuizGUI().setVisible(true));
    }
}
