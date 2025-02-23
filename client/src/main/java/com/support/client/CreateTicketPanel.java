package com.support.client;

import com.support.dto.CreateTicketRequest;
import com.support.entity.Ticket;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CreateTicketPanel extends JPanel {
    private final SupportClient client;
    private final JTextField titleField;
    private final JTextArea descriptionArea;
    private final JComboBox<Ticket.Priority> priorityComboBox;
    private final JComboBox<Ticket.Category> categoryComboBox;
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color CARD_BACKGROUND = Color.WHITE;

    public CreateTicketPanel(SupportClient client) {
        this.client = client;
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]10[]10[grow]"));
        setBackground(BACKGROUND_COLOR);

        // Create header panel
        JPanel headerPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow][]", "[]"));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Create New Ticket");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));

        // Create main form panel
        JPanel formPanel = new JPanel(new MigLayout("fillx, insets 30", "[100]10[grow]", "[]15[]15[]15[]"));
        formPanel.setBackground(CARD_BACKGROUND);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Create form components
        titleField = createStyledTextField();
        descriptionArea = createStyledTextArea();
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(8, 8, 8, 8)
        ));

        priorityComboBox = createStyledComboBox(Ticket.Priority.values());
        categoryComboBox = createStyledComboBox(Ticket.Category.values());

        // Add components to form panel
        formPanel.add(createStyledLabel("Title:"), "right");
        formPanel.add(titleField, "growx, wrap");

        formPanel.add(createStyledLabel("Description:"), "right, top");
        formPanel.add(descriptionScroll, "grow, h 150!, wrap");

        formPanel.add(createStyledLabel("Priority:"), "right");
        formPanel.add(priorityComboBox, "growx, wrap");

        formPanel.add(createStyledLabel("Category:"), "right");
        formPanel.add(categoryComboBox, "growx, wrap");

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new MigLayout("insets 20", "push[]10[]push"));
        buttonPanel.setBackground(CARD_BACKGROUND);

        JButton cancelButton = createStyledButton("Cancel", new Color(190, 190, 190), false);
        JButton submitButton = createStyledButton("Submit", PRIMARY_COLOR, true);

        cancelButton.addActionListener(e -> handleCancel());
        submitButton.addActionListener(e -> handleSubmit());

        buttonPanel.add(cancelButton, "width 120!");
        buttonPanel.add(submitButton, "width 120!");

        // Add all panels to the main panel
        headerPanel.add(titleLabel, "grow");
        add(headerPanel, "growx, wrap");
        add(formPanel, "grow, wrap");
        add(buttonPanel, "growx");
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(70, 70, 70));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMargin(new Insets(8, 10, 8, 10));
        return field;
    }

    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMargin(new Insets(8, 8, 8, 8));
        return area;
    }

    private <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(CARD_BACKGROUND);
        ((JComponent) comboBox.getRenderer()).setBorder(new EmptyBorder(5, 8, 5, 8));
        return comboBox;
    }

    private JButton createStyledButton(String text, Color backgroundColor, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", isPrimary ? Font.BOLD : Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(8, 16, 8, 16));

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

    private void handleSubmit() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        Ticket.Priority priority = (Ticket.Priority) priorityComboBox.getSelectedItem();
        Ticket.Category category = (Ticket.Category) categoryComboBox.getSelectedItem();

        if (title.isEmpty() || description.isEmpty()) {
            showError("Please fill in all required fields");
            return;
        }

        CreateTicketRequest request = new CreateTicketRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setPriority(priority);
        request.setCategory(category);

        try {
            ApiClient.createTicket(request);
            client.showTicketListPanel();
        } catch (Exception ex) {
            showError("Failed to create ticket: " + ex.getMessage());
        }
    }

    private void handleCancel() {
        client.showTicketListPanel();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
} 