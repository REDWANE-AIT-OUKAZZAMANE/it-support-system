package com.support.client;

import com.support.dto.TicketDTO;
import com.support.entity.Ticket;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class TicketListPanel extends JPanel {
    private final SupportClient client;
    private final JTable ticketTable;
    private final DefaultTableModel tableModel;
    private final JButton createTicketButton;
    private final JButton refreshButton;
    private final JComboBox<String> statusFilter;
    private final JButton updateStatusButton;
    private final JButton logoutButton;
    private final JPanel toolbarPanel;
    private final JTextField searchField;
    private final JPanel filterPanel;
    private List<TicketDTO> allTickets;
    private static final String ALL_STATUSES = "All";
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public TicketListPanel(SupportClient client, boolean loadImmediately) {
        this.client = client;
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]10[]10[]10[grow]"));
        setBackground(BACKGROUND_COLOR);

        // Create header panel
        JPanel headerPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow][]", "[]"));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Support Tickets");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        
        // Create toolbar panel with modern styling
        toolbarPanel = new JPanel(new MigLayout("", "[]10[]push[]", "[]"));
        toolbarPanel.setBackground(BACKGROUND_COLOR);
        
        createTicketButton = createStyledButton("New Ticket", PRIMARY_COLOR, true);
        refreshButton = createStyledButton("Refresh", new Color(100, 100, 100), false);
        updateStatusButton = createStyledButton("Update Status", PRIMARY_COLOR, false);
        logoutButton = createStyledButton("Logout", new Color(190, 190, 190), false);

        toolbarPanel.add(refreshButton);
        toolbarPanel.add(logoutButton, "gap push");

        // Create search and filter panel
        filterPanel = new JPanel(new MigLayout("", "[]5[]5[]5[]", "[]"));
        filterPanel.setBackground(CARD_BACKGROUND);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Style search components
        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setMargin(new Insets(5, 10, 5, 10));
        searchField.setToolTipText("Search by Ticket ID");
        
        JLabel searchLabel = createStyledLabel("Search ID:");
        JLabel filterLabel = createStyledLabel("Status:");

        // Create status filter
        DefaultComboBoxModel<String> statusModel = new DefaultComboBoxModel<>();
        statusModel.addElement(ALL_STATUSES);
        for (Ticket.Status status : Ticket.Status.values()) {
            statusModel.addElement(status.name());
        }
        statusFilter = new JComboBox<>(statusModel);
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusFilter.setBackground(CARD_BACKGROUND);
        statusFilter.setToolTipText("Filter by Status");

        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(filterLabel);
        filterPanel.add(statusFilter);

        // Create table with modern styling
        String[] columnNames = {"ID", "Title", "Description", "Priority", "Category", "Status", "Created By", "Creation Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Long.class;
                return super.getColumnClass(columnIndex);
            }
        };
        
        ticketTable = new JTable(tableModel);
        ticketTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ticketTable.setRowHeight(35);
        ticketTable.setShowGrid(false);
        ticketTable.setIntercellSpacing(new Dimension(0, 0));
        ticketTable.setSelectionBackground(new Color(232, 240, 254));
        ticketTable.setSelectionForeground(Color.BLACK);

        // Style the table header
        JTableHeader header = ticketTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(CARD_BACKGROUND);
        header.setForeground(new Color(51, 51, 51));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // Set column widths and renderers
        setupTableColumns();

        // Create a styled scroll pane
        JScrollPane scrollPane = new JScrollPane(ticketTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);

        // Add components to the main panel
        headerPanel.add(titleLabel, "grow");
        headerPanel.add(toolbarPanel, "wrap");
        
        add(headerPanel, "growx, wrap");
        add(filterPanel, "growx, wrap");
        add(scrollPane, "grow");

        // Add listeners
        setupListeners();

        // Initial load if requested
        if (loadImmediately) {
            refreshTickets();
        }
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(70, 70, 70));
        return label;
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

    private void setupTableColumns() {
        // Set up custom renderers and column widths
        ticketTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        ticketTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Title
        ticketTable.getColumnModel().getColumn(2).setPreferredWidth(300); // Description
        ticketTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Priority
        ticketTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Category
        ticketTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        ticketTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Created By
        ticketTable.getColumnModel().getColumn(7).setPreferredWidth(150); // Creation Date

        // Center all columns except Description
        CenteredTableCellRenderer centeredRenderer = new CenteredTableCellRenderer();
        ticketTable.getColumnModel().getColumn(0).setCellRenderer(centeredRenderer); // ID
        ticketTable.getColumnModel().getColumn(1).setCellRenderer(centeredRenderer); // Title
        ticketTable.getColumnModel().getColumn(4).setCellRenderer(centeredRenderer); // Category
        ticketTable.getColumnModel().getColumn(6).setCellRenderer(centeredRenderer); // Created By
        ticketTable.getColumnModel().getColumn(7).setCellRenderer(centeredRenderer); // Creation Date

        // Custom renderer for status column (already centered)
        ticketTable.getColumnModel().getColumn(5).setCellRenderer(new StatusColumnRenderer());
        
        // Custom renderer for priority column (already centered)
        ticketTable.getColumnModel().getColumn(3).setCellRenderer(new PriorityColumnRenderer());

        // Description column with line wrapping (keep left-aligned for better readability)
        ticketTable.getColumnModel().getColumn(2).setCellRenderer(new WrappingCellRenderer());

        // Center the table header text
        ((DefaultTableCellRenderer) ticketTable.getTableHeader().getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void setupListeners() {
        createTicketButton.addActionListener(e -> client.showCreateTicketPanel());
        refreshButton.addActionListener(e -> refreshTickets());
        statusFilter.addActionListener(e -> applyFilters());
        updateStatusButton.addActionListener(e -> updateSelectedTicketStatus());
        logoutButton.addActionListener(e -> handleLogout());

        ticketTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showTicketDetails();
                }
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
        });
    }

    // Custom renderer for the Status column
    private class StatusColumnRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(5, 10, 5, 10));
            
            if (value != null) {
                String status = value.toString();
                switch (status) {
                    case "NEW":
                        setColors(label, new Color(235, 245, 255), new Color(0, 120, 212), isSelected);
                        break;
                    case "IN_PROGRESS":
                        setColors(label, new Color(255, 244, 206), new Color(201, 152, 0), isSelected);
                        break;
                    case "RESOLVED":
                        setColors(label, new Color(223, 246, 221), new Color(16, 124, 16), isSelected);
                        break;
                }
            }
            return label;
        }

        private void setColors(JLabel label, Color bg, Color fg, boolean isSelected) {
            if (!isSelected) {
                label.setBackground(bg);
                label.setForeground(fg);
            }
        }
    }

    // Custom renderer for the Priority column
    private class PriorityColumnRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(5, 10, 5, 10));
            
            if (value != null) {
                String priority = value.toString();
                switch (priority) {
                    case "HIGH":
                        setColors(label, new Color(255, 235, 235), new Color(188, 0, 0), isSelected);
                        break;
                    case "MEDIUM":
                        setColors(label, new Color(255, 244, 206), new Color(201, 152, 0), isSelected);
                        break;
                    case "LOW":
                        setColors(label, new Color(223, 246, 221), new Color(16, 124, 16), isSelected);
                        break;
                }
            }
            return label;
        }

        private void setColors(JLabel label, Color bg, Color fg, boolean isSelected) {
            if (!isSelected) {
                label.setBackground(bg);
                label.setForeground(fg);
            }
        }
    }

    // Custom renderer for wrapping text in the Description column
    private class WrappingCellRenderer extends JTextArea implements TableCellRenderer {
        public WrappingCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setBorder(new EmptyBorder(5, 5, 5, 5));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");
            setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            int height = Math.min(getPreferredSize().height, 100);
            if (table.getRowHeight(row) != height) {
                table.setRowHeight(row, height);
            }
            
            return this;
        }
    }

    // Add this new renderer class after the existing renderer classes
    private class CenteredTableCellRenderer extends DefaultTableCellRenderer {
        public CenteredTableCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(new EmptyBorder(5, 5, 5, 5));
            return c;
        }
    }

    public void refreshTickets() {
        try {
            // Check IT support role and update UI accordingly
            boolean isITSupport = ApiClient.isITSupport();
            
            // Update toolbar based on role
            if (isITSupport) {
                // Add IT Support specific controls if not already present
                if (!toolbarPanel.isAncestorOf(updateStatusButton)) {
                    toolbarPanel.remove(logoutButton);
                    toolbarPanel.add(updateStatusButton);
                    toolbarPanel.add(logoutButton, "gap push");
                }
                // Remove create ticket button if present
                if (toolbarPanel.isAncestorOf(createTicketButton)) {
                    toolbarPanel.remove(createTicketButton);
                }
            } else {
                // Remove IT Support specific controls if present
                if (toolbarPanel.isAncestorOf(updateStatusButton)) {
                    toolbarPanel.remove(updateStatusButton);
                }
                // Add create ticket button if not present
                if (!toolbarPanel.isAncestorOf(createTicketButton)) {
                    toolbarPanel.add(createTicketButton, 0);
                }
            }
            
            // Refresh the toolbar layout
            toolbarPanel.revalidate();
            toolbarPanel.repaint();

            // Get and store all tickets
            allTickets = ApiClient.getTickets();
            applyFilters();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to load tickets: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilters() {
        if (allTickets == null) return;

        String searchText = searchField.getText().trim();
        String selectedStatus = (String) statusFilter.getSelectedItem();
        
        // Create a new list for filtered tickets
        List<TicketDTO> filteredTickets = new ArrayList<>();
        
        for (TicketDTO ticket : allTickets) {
            boolean matchesSearch = searchText.isEmpty() || 
                                  String.valueOf(ticket.getId()).contains(searchText);
            boolean matchesStatus = ALL_STATUSES.equals(selectedStatus) || 
                                  selectedStatus.equals(ticket.getStatus().name());

            if (matchesSearch && matchesStatus) {
                filteredTickets.add(ticket);
            }
        }

        // Sort filtered tickets by creation date (newest first)
        filteredTickets.sort((t1, t2) -> t2.getCreationDate().compareTo(t1.getCreationDate()));
        
        // Update table model
        tableModel.setRowCount(0);
        for (TicketDTO ticket : filteredTickets) {
            Object[] row = {
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority().name(),
                ticket.getCategory().name(),
                ticket.getStatus().name(),
                ticket.getCreatedByUsername(),
                ticket.getCreationDate()
            };
            tableModel.addRow(row);
        }
    }

    private void showTicketDetails() {
        int selectedRow = ticketTable.getSelectedRow();
        if (selectedRow == -1) return;

        // Get ticket data from the selected row
        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);
        String description = (String) tableModel.getValueAt(selectedRow, 2);
        String priority = (String) tableModel.getValueAt(selectedRow, 3);
        String category = (String) tableModel.getValueAt(selectedRow, 4);
        String status = (String) tableModel.getValueAt(selectedRow, 5);
        String createdBy = (String) tableModel.getValueAt(selectedRow, 6);
        String creationDate = tableModel.getValueAt(selectedRow, 7).toString();

        // Create and configure the dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ticket Details", true);
        dialog.setBackground(BACKGROUND_COLOR);
        
        // Create main content panel
        JPanel contentPanel = new JPanel(new MigLayout("fillx, insets 30", "[150][grow]", "[]15[]15[]15[]15[]15[]15[]15[]"));
        contentPanel.setBackground(CARD_BACKGROUND);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Add title at the top
        JLabel titleHeader = new JLabel("Ticket Information");
        titleHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleHeader.setForeground(new Color(51, 51, 51));
        contentPanel.add(titleHeader, "span 2, center, gapbottom 20, wrap");

        // Add fields with styled labels and values
        addDetailField(contentPanel, "Ticket ID:", id.toString());
        addDetailField(contentPanel, "Title:", title);
        
        // Description with text area
        contentPanel.add(createStyledLabel("Description:"), "top");
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(new Color(250, 250, 250));
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(300, 100));
        contentPanel.add(descScroll, "grow, wrap");

        // Add status with colored label and combo box for admin
        contentPanel.add(createStyledLabel("Status:"), "");
        JPanel statusPanel = new JPanel(new MigLayout("insets 0", "[]10[]", "[]"));
        statusPanel.setBackground(CARD_BACKGROUND);

        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(new EmptyBorder(5, 15, 5, 15));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        switch (status) {
            case "NEW":
                styleStatusLabel(statusLabel, new Color(235, 245, 255), new Color(0, 120, 212));
                break;
            case "IN_PROGRESS":
                styleStatusLabel(statusLabel, new Color(255, 244, 206), new Color(201, 152, 0));
                break;
            case "RESOLVED":
                styleStatusLabel(statusLabel, new Color(223, 246, 221), new Color(16, 124, 16));
                break;
        }
        statusPanel.add(statusLabel);

        // Add status update button for admin
        if (ApiClient.isITSupport()) {
            JButton updateStatusButton = createStyledButton("Change Status", PRIMARY_COLOR, false);
            updateStatusButton.addActionListener(e -> {
                // Create status selection dialog
                JDialog statusDialog = new JDialog(dialog, "Update Status", true);
                statusDialog.setBackground(BACKGROUND_COLOR);

                JPanel statusDialogPanel = new JPanel(new MigLayout("fillx, insets 20", "[grow]", "[]10[]20[]"));
                statusDialogPanel.setBackground(CARD_BACKGROUND);
                statusDialogPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                    new EmptyBorder(10, 10, 10, 10)
                ));

                JLabel statusHeaderLabel = new JLabel("Select New Status");
                statusHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                statusDialogPanel.add(statusHeaderLabel, "center, wrap");

                JComboBox<Ticket.Status> statusComboBox = new JComboBox<>(Ticket.Status.values());
                statusComboBox.setSelectedItem(Ticket.Status.valueOf(status));
                statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                statusComboBox.setBackground(CARD_BACKGROUND);
                statusDialogPanel.add(statusComboBox, "growx, wrap");

                JPanel statusButtonPanel = new JPanel(new MigLayout("insets 0", "push[]10[]push", "[]"));
                statusButtonPanel.setBackground(CARD_BACKGROUND);
                
                JButton cancelStatusButton = createStyledButton("Cancel", new Color(190, 190, 190), false);
                JButton updateButton = createStyledButton("Update", PRIMARY_COLOR, true);

                cancelStatusButton.addActionListener(evt -> statusDialog.dispose());
                updateButton.addActionListener(evt -> {
                    Ticket.Status newStatus = (Ticket.Status) statusComboBox.getSelectedItem();
                    if (newStatus != Ticket.Status.valueOf(status)) {
                        try {
                            ApiClient.updateTicketStatus(id, newStatus);
                            statusLabel.setText(newStatus.name());
                            // Update status label colors
                            switch (newStatus.name()) {
                                case "NEW":
                                    styleStatusLabel(statusLabel, new Color(235, 245, 255), new Color(0, 120, 212));
                                    break;
                                case "IN_PROGRESS":
                                    styleStatusLabel(statusLabel, new Color(255, 244, 206), new Color(201, 152, 0));
                                    break;
                                case "RESOLVED":
                                    styleStatusLabel(statusLabel, new Color(223, 246, 221), new Color(16, 124, 16));
                                    break;
                            }
                            refreshTickets();
                            statusDialog.dispose();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(statusDialog,
                                "Failed to update ticket status: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        statusDialog.dispose();
                    }
                });

                statusButtonPanel.add(cancelStatusButton, "width 100!");
                statusButtonPanel.add(updateButton, "width 100!");
                statusDialogPanel.add(statusButtonPanel, "growx");

                statusDialog.add(statusDialogPanel);
                statusDialog.pack();
                statusDialog.setLocationRelativeTo(dialog);
                statusDialog.setResizable(false);
                statusDialog.setVisible(true);
            });
            statusPanel.add(updateStatusButton);
        }
        contentPanel.add(statusPanel, "wrap");

        // Add priority with colored label
        contentPanel.add(createStyledLabel("Priority:"), "");
        JLabel priorityLabel = new JLabel(priority);
        priorityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priorityLabel.setOpaque(true);
        priorityLabel.setBorder(new EmptyBorder(5, 15, 5, 15));
        priorityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        switch (priority) {
            case "HIGH":
                styleStatusLabel(priorityLabel, new Color(255, 235, 235), new Color(188, 0, 0));
                break;
            case "MEDIUM":
                styleStatusLabel(priorityLabel, new Color(255, 244, 206), new Color(201, 152, 0));
                break;
            case "LOW":
                styleStatusLabel(priorityLabel, new Color(223, 246, 221), new Color(16, 124, 16));
                break;
        }
        contentPanel.add(priorityLabel, "wrap");

        addDetailField(contentPanel, "Category:", category);
        addDetailField(contentPanel, "Created By:", createdBy);
        addDetailField(contentPanel, "Creation Date:", creationDate);

        // Add buttons panel
        JPanel buttonPanel = new JPanel(new MigLayout("insets 20", "[center, grow]"));
        buttonPanel.setBackground(CARD_BACKGROUND);
        
        JButton closeButton = createStyledButton("Close", new Color(100, 100, 100), false);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton, "width 120!");

        // Add panels to dialog
        dialog.setLayout(new BorderLayout());
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog properties
        dialog.pack();
        dialog.setSize(550, dialog.getHeight());
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void addDetailField(JPanel panel, String label, String value) {
        panel.add(createStyledLabel(label), "");
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(valueLabel, "wrap");
    }

    private void styleStatusLabel(JLabel label, Color bg, Color fg) {
        label.setBackground(bg);
        label.setForeground(fg);
    }

    private void updateSelectedTicketStatus() {
        int selectedRow = ticketTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a ticket to update",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long ticketId = (Long) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);
        Ticket.Status currentStatus = Ticket.Status.valueOf(tableModel.getValueAt(selectedRow, 5).toString());

        // Create and configure the dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update Ticket Status", true);
        dialog.setBackground(BACKGROUND_COLOR);

        // Create main content panel
        JPanel contentPanel = new JPanel(new MigLayout("fillx, insets 30", "[grow]", "[]20[]20[]"));
        contentPanel.setBackground(CARD_BACKGROUND);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Add header
        JLabel headerLabel = new JLabel("Update Status");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(headerLabel, "center, wrap");

        // Add ticket info
        JPanel ticketInfoPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow]", "[]5[]"));
        ticketInfoPanel.setBackground(CARD_BACKGROUND);
        JLabel ticketIdLabel = new JLabel("Ticket #" + ticketId);
        ticketIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel ticketTitleLabel = new JLabel(title);
        ticketTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ticketInfoPanel.add(ticketIdLabel, "wrap");
        ticketInfoPanel.add(ticketTitleLabel, "wrap");
        contentPanel.add(ticketInfoPanel, "grow, wrap");

        // Create status selection panel
        JPanel statusPanel = new JPanel(new MigLayout("fillx, insets 0", "[right]10[grow]", "[]"));
        statusPanel.setBackground(CARD_BACKGROUND);
        JLabel statusLabel = createStyledLabel("New Status:");
        JComboBox<Ticket.Status> statusComboBox = new JComboBox<>(Ticket.Status.values());
        statusComboBox.setSelectedItem(currentStatus);
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusComboBox.setBackground(CARD_BACKGROUND);
        
        statusPanel.add(statusLabel);
        statusPanel.add(statusComboBox, "growx");
        contentPanel.add(statusPanel, "grow, wrap");

        // Add buttons
        JPanel buttonPanel = new JPanel(new MigLayout("insets 20", "push[]10[]push"));
        buttonPanel.setBackground(CARD_BACKGROUND);
        
        JButton cancelButton = createStyledButton("Cancel", new Color(190, 190, 190), false);
        JButton updateButton = createStyledButton("Update", PRIMARY_COLOR, true);
        
        cancelButton.addActionListener(e -> dialog.dispose());
        updateButton.addActionListener(e -> {
            Ticket.Status selectedStatus = (Ticket.Status) statusComboBox.getSelectedItem();
            if (selectedStatus != currentStatus) {
                try {
                    ApiClient.updateTicketStatus(ticketId, selectedStatus);
                    refreshTickets();
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to update ticket status: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                dialog.dispose();
            }
        });

        buttonPanel.add(cancelButton, "width 120!");
        buttonPanel.add(updateButton, "width 120!");

        // Add panels to dialog
        dialog.setLayout(new BorderLayout());
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog properties
        dialog.pack();
        dialog.setSize(400, dialog.getHeight());
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void handleLogout() {
        ApiClient.setCredentials(null, null);
        client.showLoginPanel();
    }
} 