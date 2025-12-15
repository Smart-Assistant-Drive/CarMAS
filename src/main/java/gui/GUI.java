package gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    // First page components
    private JTextField licenseField = new JTextField(15);

    // Second page components
    private JTextArea infoArea = new JTextArea(5, 20);
    private JLabel speedLabel = new JLabel("Speed: 0");
    private boolean autonomous = false;
    private double speed = 0;

    private final GUIEventInterface guiEventInterface;

    public GUI(GUIEventInterface guiEventInterface) {
        this.guiEventInterface = guiEventInterface;
        setTitle("Car Control");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);

        // First page
        JPanel page1 = new JPanel();
        page1.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        page1.add(new JLabel("License Number:"), gbc);
        gbc.gridx = 1;
        page1.add(licenseField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start Car");
        page1.add(startButton, gbc);

        // Second page
        JPanel page2 = new JPanel();
        page2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JButton stopButton = new JButton("Stop Car");
        page2.add(stopButton, gbc);

        gbc.gridx = 1;
        JToggleButton autonomousToggle = new JToggleButton("Autonomous: OFF");
        page2.add(autonomousToggle, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JButton increaseSpeed = new JButton("Increase Speed");
        page2.add(increaseSpeed, gbc);

        gbc.gridx = 1;
        JButton decreaseSpeed = new JButton("Decrease Speed");
        page2.add(decreaseSpeed, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        page2.add(speedLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(infoArea);
        page2.add(scrollPane, gbc);

        // Add pages to main panel
        mainPanel.add(page1, "page1");
        mainPanel.add(page2, "page2");
        add(mainPanel);

        // Button actions
        startButton.addActionListener(e -> {
                    cardLayout.show(mainPanel, "page2");
                    guiEventInterface.onInsertLicense(licenseField.getText());
                    guiEventInterface.onStartCar();
                }
        );
        stopButton.addActionListener(e -> {
                    cardLayout.show(mainPanel, "page1");
                    autonomous = false;
                    autonomousToggle.setSelected(false);
                    autonomousToggle.setText("Autonomous: OFF");
                    guiEventInterface.onStopCar();
                }
        );

        autonomousToggle.addActionListener(e -> {
            autonomous = autonomousToggle.isSelected();
            autonomousToggle.setText("Autonomous: " + (autonomous ? "ON" : "OFF"));
            if (!autonomous) {
                increaseSpeed.setEnabled(true);
                decreaseSpeed.setEnabled(true);
            }else{
                increaseSpeed.setEnabled(false);
                decreaseSpeed.setEnabled(false);
            }
            guiEventInterface.onToggleAutonomousModeChange(autonomous);
        });

        increaseSpeed.addActionListener(e -> {
            speed++;
            speedLabel.setText("Speed: " + speed);
            guiEventInterface.onSpeedChange(speed);
        });

        decreaseSpeed.addActionListener(e -> {
            speed = Math.max(0, speed - 1);
            speedLabel.setText("Speed: " + speed);
            guiEventInterface.onSpeedChange(speed);
        });
    }

    public void changeSpeed(double newSpeed) {
        this.speed = newSpeed;
        speedLabel.setText("Speed: " + speed);
    }

    public void changeInfo(String info) {
        infoArea.setText(info);
    }
}

