package com.support.client;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Base64;

public class LoginPanel extends JPanel {
    private final SupportClient client;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton clearButton;

    public LoginPanel(SupportClient client) {
        this.client = client;
        // Use MigLayout with fill and center alignment
        setLayout(new MigLayout("fill, wrap 1, insets 0", "[center, grow]", "push[]25[]25[]25[]25[]push"));
        setBackground(new Color(245, 245, 245)); // Light gray background

        // Create a container panel for the login form
        JPanel formPanel = new JPanel(new MigLayout("wrap 2, insets 30", "[][grow,fill]", "[]20[]"));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(20, 40, 20, 40)
        ));

        // Create components
        JLabel titleLabel = new JLabel("IT Support System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(51, 51, 51));

        JLabel subtitleLabel = new JLabel("Sign in to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(102, 102, 102));

        usernameField = createStyledTextField();
        passwordField = createStyledPasswordField();
        loginButton = createStyledButton("Sign In", new Color(70, 130, 180)); // Steel blue color
        clearButton = createStyledButton("Clear", new Color(190, 190, 190));

        // Add components to the form panel
        formPanel.add(new JLabel("Username"), "right");
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password"), "right");
        formPanel.add(passwordField);

        // Create button panel
        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "[grow][grow]", "[]"));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton, "width 120!, height 35!");
        buttonPanel.add(clearButton, "width 120!, height 35!");

        // Add all components to the main panel
        add(titleLabel, "center");
        add(subtitleLabel, "center");
        add(formPanel, "width 400!");
        add(buttonPanel, "width 400!");

        // Add action listeners
        loginButton.addActionListener(e -> handleLogin());
        clearButton.addActionListener(e -> handleClear());
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMargin(new Insets(5, 10, 5, 10));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMargin(new Insets(5, 10, 5, 10));
        return field;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showErrorDialog("Please enter both username and password");
            return;
        }

        // Store credentials for API calls
        ApiClient.setCredentials(username, password);

        // Try to get tickets to verify credentials
        try {
            ApiClient.getTickets();
            client.showTicketListPanel();
        } catch (Exception ex) {
            showErrorDialog("Invalid credentials. Please try again.");
            passwordField.setText("");
        }
    }

    private void handleClear() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocus();
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Login Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
} 