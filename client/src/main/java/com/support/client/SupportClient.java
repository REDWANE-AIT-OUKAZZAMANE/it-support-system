package com.support.client;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import java.awt.*;

public class SupportClient {
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private TicketListPanel ticketListPanel;
    private CreateTicketPanel createTicketPanel;

    public SupportClient() {
        initializeUI();
    }

    private void initializeUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Add custom UI defaults
            UIManager.put("TextField.arc", 10);
            UIManager.put("Button.arc", 10);
            UIManager.put("Panel.background", new Color(245, 245, 245));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainFrame = new JFrame("IT Support System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(900, 650);
        mainFrame.setMinimumSize(new Dimension(800, 600));

        // Center the window on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - mainFrame.getWidth()) / 2;
        int y = (screenSize.height - mainFrame.getHeight()) / 2;
        mainFrame.setLocation(x, y);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(245, 245, 245));

        // Initialize login panel first
        loginPanel = new LoginPanel(this);
        mainPanel.add(loginPanel, "LOGIN");

        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
        showLoginPanel();
    }

    public void showLoginPanel() {
        cardLayout.show(mainPanel, "LOGIN");
        mainFrame.setTitle("IT Support System - Login");
    }

    public void showTicketListPanel() {
        if (ticketListPanel == null) {
            ticketListPanel = new TicketListPanel(this, false);
            mainPanel.add(ticketListPanel, "TICKET_LIST");
        }
        cardLayout.show(mainPanel, "TICKET_LIST");
        mainFrame.setTitle("IT Support System - Tickets");
        ticketListPanel.refreshTickets();
    }

    public void showCreateTicketPanel() {
        if (createTicketPanel == null) {
            createTicketPanel = new CreateTicketPanel(this);
            mainPanel.add(createTicketPanel, "CREATE_TICKET");
        }
        cardLayout.show(mainPanel, "CREATE_TICKET");
        mainFrame.setTitle("IT Support System - Create Ticket");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SupportClient());
    }
} 