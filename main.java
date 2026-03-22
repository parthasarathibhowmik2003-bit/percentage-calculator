import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DualCalculator extends JFrame {
    
    // Colors
    private static final Color BG_COLOR = new Color(15, 23, 42);
    private static final Color PANEL_BG = new Color(30, 41, 59, 179);
    private static final Color ACCENT_COLOR = new Color(99, 102, 241);
    private static final Color TEXT_COLOR = new Color(226, 232, 240);
    private static final Color INPUT_BG = new Color(15, 23, 42, 153);
    private static final Color BORDER_COLOR = new Color(51, 65, 85);
    private static final Color EMERALD = new Color(52, 211, 153);
    private static final Color ROSE = new Color(244, 63, 94);
    private static final Color AMBER = new Color(251, 191, 36);
    private static final Color CYAN = new Color(34, 211, 238);
    
    // Percentage calculator variables
    private String percentOperation = "add";
    private List<ChainItem> chain = new ArrayList<>();
    private double runningTotal = 0;
    
    // Standard calculator variables
    private String calcDisplay = "0";
    private String calcPrevious = "";
    private String calcOperation = null;
    private boolean calcResetNext = false;
    private List<String> calcHistory = new ArrayList<>();
    
    // UI Components - Percentage Calculator
    private JTextField baseInput;
    private JTextField percentInput;
    private JLabel percentDisplay;
    private JLabel percentFormula;
    private JPanel chainListPanel;
    private JPanel chainTotalPanel;
    private JLabel runningTotalLabel;
    private JButton[] opButtons = new JButton[4];
    
    // UI Components - Standard Calculator
    private JLabel calcDisplayLabel;
    private JLabel calcPreviousLabel;
    private JPanel calcHistoryPanel;
    
    // Operation symbols
    private final String[] OP_SYMBOLS = {"+", "−", "×", "÷"};
    private final String[] OP_NAMES = {"add", "subtract", "multiply", "divide"};
    
    public DualCalculator() {
        setTitle("Dual Calculator Suite | Percentage & Standard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Main panel with background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background gradient
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(15, 23, 42),
                    getWidth(), getHeight(), new Color(30, 41, 59)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BG_COLOR);
        
        // Header
        JPanel headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center panel with two columns
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);
        
        // Left: Percentage Calculator
        JPanel percentPanel = createPercentageCalculator();
        centerPanel.add(percentPanel);
        
        // Right: Standard Calculator
        JPanel standardPanel = createStandardCalculator();
        centerPanel.add(standardPanel);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Keyboard listener
        setupKeyboardListener();
    }
    
    private JPanel createHeader() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel title = new JLabel("Dual Calculator Suite");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(165, 180, 252));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Gradient effect for title
        title.setForeground(new Color(196, 181, 253));
        
        JLabel subtitle = new JLabel("Advanced Percentage Engine & Standard Calculator");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(148, 163, 184));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(subtitle);
        
        return panel;
    }
    
    private JPanel createPercentageCalculator() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(99, 102, 241, 51), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Top accent line
        JPanel accentLine = new JPanel();
        accentLine.setBackground(new Color(99, 102, 241));
        accentLine.setPreferredSize(new Dimension(0, 4));
        accentLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        panel.add(accentLine);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel title = new JLabel("📊 Percentage Calculator");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        
        JButton clearBtn = new JButton("Clear All");
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        clearBtn.setForeground(new Color(148, 163, 184));
        clearBtn.setBackground(new Color(0, 0, 0, 0));
        clearBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> clearPercentage());
        
        header.add(title, BorderLayout.WEST);
        header.add(clearBtn, BorderLayout.EAST);
        panel.add(header);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Display box
        JPanel displayBox = createDisplayBox();
        percentDisplay = new JLabel("0");
        percentDisplay.setFont(new Font("JetBrains Mono", Font.BOLD, 24));
        percentDisplay.setForeground(Color.WHITE);
        
        JLabel label = new JLabel("Current Calculation");
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(new Color(100, 116, 139));
        
        percentFormula = new JLabel("");
        percentFormula.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        percentFormula.setForeground(new Color(129, 140, 248));
        
        displayBox.add(label);
        displayBox.add(Box.createRigidArea(new Dimension(0, 5)));
        displayBox.add(percentDisplay);
        displayBox.add(Box.createRigidArea(new Dimension(0, 5)));
        displayBox.add(percentFormula);
        panel.add(displayBox);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Base input
        JPanel baseGroup = createInputGroup("BASE VALUE", new Color(129, 140, 248));
        baseInput = new JTextField();
        baseInput.setFont(new Font("JetBrains Mono", Font.PLAIN, 18));
        baseInput.setBackground(INPUT_BG);
        baseInput.setForeground(Color.WHITE);
        baseInput.setCaretColor(Color.WHITE);
        baseInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(148, 163, 184, 51), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        baseInput.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updatePercentDisplay();
                if (e.getKeyCode() == KeyEvent.VK_ENTER) calculatePercentage();
            }
        });
        baseGroup.add(baseInput);
        panel.add(baseGroup);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Operation buttons
        JPanel opPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        opPanel.setOpaque(false);
        opPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        Color[] opColors = {EMERALD, ROSE, AMBER, CYAN};
        for (int i = 0; i < 4; i++) {
            final String op = OP_NAMES[i];
            JButton btn = new JButton(OP_SYMBOLS[i]);
            btn.setFont(new Font("SansSerif", Font.BOLD, 20));
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(51, 65, 85));
            btn.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            if (i == 0) { // Default active
                btn.setBackground(new Color(99, 102, 241));
                btn.setBorder(BorderFactory.createEmptyBorder());
            }
            
            final int idx = i;
            btn.addActionListener(e -> setPercentOp(op, idx, opColors[idx]));
            opButtons[i] = btn;
            opPanel.add(btn);
        }
        panel.add(opPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Percentage input
        JPanel percentGroup = createInputGroup("PERCENTAGE (%)", new Color(236, 72, 153));
        percentInput = new JTextField();
        percentInput.setFont(new Font("JetBrains Mono", Font.PLAIN, 18));
        percentInput.setBackground(INPUT_BG);
        percentInput.setForeground(Color.WHITE);
        percentInput.setCaretColor(Color.WHITE);
        percentInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(148, 163, 184, 51), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        percentInput.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updatePercentDisplay();
                if (e.getKeyCode() == KeyEvent.VK_ENTER) calculatePercentage();
            }
        });
        percentGroup.add(percentInput);
        panel.add(percentGroup);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Presets
        JPanel presets = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        presets.setOpaque(false);
        presets.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel quickLabel = new JLabel("Quick:");
        quickLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        quickLabel.setForeground(new Color(148, 163, 184));
        presets.add(quickLabel);
        
        int[] presetValues = {10, 25, 50, 100};
        for (int val : presetValues) {
            JButton btn = new JButton(val + "%");
            btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
            btn.setForeground(new Color(148, 163, 184));
            btn.setBackground(new Color(51, 65, 85));
            btn.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> setPreset(val));
            presets.add(btn);
        }
        panel.add(presets);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Calculate button
        JButton calcBtn = new JButton("Calculate & Chain");
        calcBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        calcBtn.setForeground(Color.WHITE);
        calcBtn.setBackground(new Color(79, 70, 229));
        calcBtn.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));
        calcBtn.setFocusPainted(false);
        calcBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calcBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        // Gradient effect
        calcBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                calcBtn.setBackground(new Color(99, 102, 241));
            }
            public void mouseExited(MouseEvent e) {
                calcBtn.setBackground(new Color(79, 70, 229));
            }
        });
        
        calcBtn.addActionListener(e -> calculatePercentage());
        panel.add(calcBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Chain section
        JPanel chainSection = new JPanel();
        chainSection.setLayout(new BoxLayout(chainSection, BoxLayout.Y_AXIS));
        chainSection.setOpaque(false);
        chainSection.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            "Calculation Chain",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            new Color(148, 163, 184)
        ));
        
        // Chain header with reset
        JPanel chainHeader = new JPanel(new BorderLayout());
        chainHeader.setOpaque(false);
        chainHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JButton resetBtn = new JButton("Reset Chain");
        resetBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        resetBtn.setForeground(ROSE);
        resetBtn.setBackground(new Color(0, 0, 0, 0));
        resetBtn.setBorder(BorderFactory.createEmptyBorder());
        resetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetBtn.addActionListener(e -> resetChain());
        chainHeader.add(resetBtn, BorderLayout.EAST);
        chainSection.add(chainHeader);
        chainSection.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Chain list (scrollable)
        chainListPanel = new JPanel();
        chainListPanel.setLayout(new BoxLayout(chainListPanel, BoxLayout.Y_AXIS));
        chainListPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(chainListPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        chainSection.add(scrollPane);
        
        // Empty chain message
        showEmptyChain();
        
        // Chain total
        chainTotalPanel = new JPanel(new BorderLayout());
        chainTotalPanel.setOpaque(false);
        chainTotalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 0, 0, 0)
        ));
        chainTotalPanel.setVisible(false);
        
        JLabel totalLabel = new JLabel("Running Total:");
        totalLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        totalLabel.setForeground(new Color(148, 163, 184));
        
        runningTotalLabel = new JLabel("0");
        runningTotalLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 20));
        runningTotalLabel.setForeground(EMERALD);
        
        chainTotalPanel.add(totalLabel, BorderLayout.WEST);
        chainTotalPanel.add(runningTotalLabel, BorderLayout.EAST);
        chainSection.add(chainTotalPanel);
        
        panel.add(chainSection);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createStandardCalculator() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel title = new JLabel("🧮 Standard Calculator");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        
        header.add(title, BorderLayout.WEST);
        panel.add(header);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Display
        JPanel displayBox = createDisplayBox();
        displayBox.setLayout(new BoxLayout(displayBox, BoxLayout.Y_AXIS));
        
        calcPreviousLabel = new JLabel("");
        calcPreviousLabel.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        calcPreviousLabel.setForeground(new Color(100, 116, 139));
        calcPreviousLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        calcDisplayLabel = new JLabel("0");
        calcDisplayLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 32));
        calcDisplayLabel.setForeground(Color.WHITE);
        calcDisplayLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        displayBox.add(calcPreviousLabel);
        displayBox.add(Box.createRigidArea(new Dimension(0, 5)));
        displayBox.add(calcDisplayLabel);
        panel.add(displayBox);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Keypad
        JPanel keypad = new JPanel(new GridLayout(5, 4, 12, 12));
        keypad.setOpaque(false);
        keypad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        
        String[] buttons = {
            "AC", "DEL", "", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "−",
            "1", "2", "3", "+",
            "0", ".", "=", ""
        };
        
        for (String text : buttons) {
            if (text.isEmpty()) {
                JPanel empty = new JPanel();
                empty.setOpaque(false);
                keypad.add(empty);
                continue;
            }
            
            JButton btn = new JButton(text);
            btn.setFont(new Font("SansSerif", Font.BOLD, 18));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            if (text.equals("AC")) {
                btn.setForeground(ROSE);
                btn.setBackground(new Color(51, 65, 85, 204));
                btn.addActionListener(e -> clearCalc());
            } else if (text.equals("DEL")) {
                btn.setForeground(TEXT_COLOR);
                btn.setBackground(new Color(51, 65, 85, 204));
                btn.addActionListener(e -> deleteLast());
            } else if (text.equals("=")) {
                btn.setForeground(Color.WHITE);
                btn.setBackground(ACCENT_COLOR);
                btn.addActionListener(e -> calculate());
            } else if ("÷×−+".contains(text)) {
                btn.setForeground(new Color(129, 140, 248));
                btn.setBackground(new Color(99, 102, 241, 51));
                final String op = text.equals("÷") ? "/" : text.equals("×") ? "*" : text.equals("−") ? "-" : "+";
                btn.addActionListener(e -> appendOperator(op));
            } else {
                btn.setForeground(Color.WHITE);
                btn.setBackground(new Color(51, 65, 85, 204));
                btn.addActionListener(e -> appendNumber(text));
            }
            
            if (text.equals("0")) {
                // Make 0 button span 2 columns visually by adding empty next to it
                // Actually GridLayout doesn't support spanning, so we handle differently
            }
            
            keypad.add(btn);
        }
        
        panel.add(keypad);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // History
        JPanel historySection = new JPanel();
        historySection.setLayout(new BoxLayout(historySection, BoxLayout.Y_AXIS));
        historySection.setOpaque(false);
        historySection.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            "Recent Results",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            new Color(148, 163, 184)
        ));
        
        JPanel historyHeader = new JPanel(new BorderLayout());
        historyHeader.setOpaque(false);
        JButton clearHistBtn = new JButton("Clear");
        clearHistBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        clearHistBtn.setForeground(new Color(100, 116, 139));
        clearHistBtn.setBackground(new Color(0, 0, 0, 0));
        clearHistBtn.setBorder(BorderFactory.createEmptyBorder());
        clearHistBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearHistBtn.addActionListener(e -> clearCalcHistory());
        historyHeader.add(clearHistBtn, BorderLayout.EAST);
        historySection.add(historyHeader);
        historySection.add(Box.createRigidArea(new Dimension(0, 5)));
        
        calcHistoryPanel = new JPanel();
        calcHistoryPanel.setLayout(new BoxLayout(calcHistoryPanel, BoxLayout.Y_AXIS));
        calcHistoryPanel.setOpaque(false);
        
        JScrollPane histScroll = new JScrollPane(calcHistoryPanel);
        histScroll.setOpaque(false);
        histScroll.getViewport().setOpaque(false);
        histScroll.setBorder(BorderFactory.createEmptyBorder());
        histScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        historySection.add(histScroll);
        
        showEmptyCalcHistory();
        
        panel.add(historySection);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createDisplayBox() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(15, 23, 42, 204));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        return panel;
    }
    
    private JPanel createInputGroup(String labelText, Color labelColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(labelColor);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        return panel;
    }
    
    // ==================== PERCENTAGE CALCULATOR METHODS ====================
    
    private void setPercentOp(String op, int index, Color color) {
        percentOperation = op;
        
        // Reset all buttons
        for (JButton btn : opButtons) {
            btn.setBackground(new Color(51, 65, 85));
            btn.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        }
        
        // Highlight active
        opButtons[index].setBackground(new Color(99, 102, 241));
        opButtons[index].setBorder(BorderFactory.createEmptyBorder());
        
        updatePercentDisplay();
    }
    
    private void setPreset(int val) {
        percentInput.setText(String.valueOf(val));
        updatePercentDisplay();
    }
    
    private void updatePercentDisplay() {
        String base = baseInput.getText().isEmpty() ? "0" : baseInput.getText();
        String percent = percentInput.getText().isEmpty() ? "0" : percentInput.getText();
        String symbol = OP_SYMBOLS[getOpIndex(percentOperation)];
        
        percentDisplay.setText(base + " " + symbol + " " + percent + "%");
        percentFormula.setText("Press Calculate to execute");
    }
    
    private int getOpIndex(String op) {
        for (int i = 0; i < OP_NAMES.length; i++) {
            if (OP_NAMES[i].equals(op)) return i;
        }
        return 0;
    }
    
    private String formatNum(double num) {
        if (num == (int) num) return String.valueOf((int) num);
        DecimalFormat df = new DecimalFormat("#.####");
        return df.format(num);
    }
    
    private void calculatePercentage() {
        double base, percent;
        try {
            base = Double.parseDouble(baseInput.getText());
            percent = Double.parseDouble(percentInput.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers");
            return;
        }
        
        double result = 0;
        
        switch(percentOperation) {
            case "add":
                result = base + (base * percent / 100);
                break;
            case "subtract":
                result = base - (base * percent / 100);
                break;
            case "multiply":
                result = base * (percent / 100);
                break;
            case "divide":
                if (percent == 0) {
                    JOptionPane.showMessageDialog(this, "Cannot divide by zero");
                    return;
                }
                result = base / (percent / 100);
                break;
        }
        
        ChainItem item = new ChainItem(base, percent, percentOperation, result);
        chain.add(item);
        runningTotal += result;
        
        renderChain();
        
        baseInput.setText(formatNum(result));
        percentInput.setText("");
        percentInput.requestFocus();
        updatePercentDisplay();
    }
    
    private void renderChain() {
        chainListPanel.removeAll();
        
        if (chain.isEmpty()) {
            showEmptyChain();
            chainTotalPanel.setVisible(false);
            return;
        }
        
        Color[] opColors = {EMERALD, ROSE, AMBER, CYAN};
        
        for (int i = 0; i < chain.size(); i++) {
            ChainItem item = chain.get(i);
            int opIdx = getOpIndex(item.op);
            
            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setOpaque(false);
            itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));
            itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            
            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            left.setOpaque(false);
            
            JLabel num = new JLabel((i + 1) + ".");
            num.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
            num.setForeground(new Color(100, 116, 139));
            
            JLabel base = new JLabel(formatNum(item.base));
            base.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
            base.setForeground(Color.WHITE);
            
            JLabel op = new JLabel(OP_SYMBOLS[opIdx]);
            op.setFont(new Font("SansSerif", Font.BOLD, 14));
            op.setForeground(opColors[opIdx]);
            
            JLabel pct = new JLabel(item.percent + "%");
            pct.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
            pct.setForeground(new Color(129, 140, 248));
            
            left.add(num);
            left.add(base);
            left.add(op);
            left.add(pct);
            
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            right.setOpaque(false);
            
            JLabel eq = new JLabel("=");
            eq.setFont(new Font("SansSerif", Font.PLAIN, 12));
            eq.setForeground(new Color(100, 116, 139));
            
            JLabel res = new JLabel(formatNum(item.result));
            res.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
            res.setForeground(EMERALD);
            
            JButton remove = new JButton("×");
            remove.setFont(new Font("SansSerif", Font.BOLD, 14));
            remove.setForeground(ROSE);
            remove.setBackground(new Color(0, 0, 0, 0));
            remove.setBorder(BorderFactory.createEmptyBorder());
            remove.setCursor(new Cursor(Cursor.HAND_CURSOR));
            final int idx = i;
            remove.addActionListener(e -> removeFromChain(idx));
            
            right.add(eq);
            right.add(res);
            right.add(remove);
            
            itemPanel.add(left, BorderLayout.WEST);
            itemPanel.add(right, BorderLayout.EAST);
            
            chainListPanel.add(itemPanel);
            chainListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        runningTotalLabel.setText(formatNum(runningTotal));
        chainTotalPanel.setVisible(true);
        
        chainListPanel.revalidate();
        chainListPanel.repaint();
    }
    
    private void showEmptyChain() {
        chainListPanel.removeAll();
        JLabel empty = new JLabel("Start calculating to build a chain");
        empty.setFont(new Font("SansSerif", Font.ITALIC, 14));
        empty.setForeground(new Color(71, 85, 105));
        empty.setAlignmentX(Component.CENTER_ALIGNMENT);
        chainListPanel.add(empty);
        chainListPanel.revalidate();
        chainListPanel.repaint();
    }
    
    private void removeFromChain(int index) {
        runningTotal -= chain.get(index).result;
        chain.remove(index);
        renderChain();
    }
    
    private void resetChain() {
        chain.clear();
        runningTotal = 0;
        baseInput.setText("");
        percentInput.setText("");
        renderChain();
        updatePercentDisplay();
    }
    
    private void clearPercentage() {
        resetChain();
    }
    
    // ==================== STANDARD CALCULATOR METHODS ====================
    
    private void appendNumber(String num) {
        if (calcResetNext) {
            calcDisplay = num;
            calcResetNext = false;
        } else {
            calcDisplay = calcDisplay.equals("0") ? num : calcDisplay + num;
        }
        updateCalcDisplay();
    }
    
    private void appendOperator(String op) {
        if (calcOperation != null && !calcResetNext) {
            calculate();
        }
        calcPrevious = calcDisplay + " " + op;
        calcOperation = op;
        calcResetNext = true;
        updateCalcDisplay();
    }
    
    private void calculate() {
        if (calcOperation == null || calcResetNext) return;
        
        double prev, current;
        try {
            prev = Double.parseDouble(calcPrevious.replaceAll(" [+\\-*/=]", ""));
            current = Double.parseDouble(calcDisplay);
        } catch (Exception e) {
            return;
        }
        
        double result = 0;
        switch(calcOperation) {
            case "+": result = prev + current; break;
            case "-": result = prev - current; break;
            case "*": result = prev * current; break;
            case "/": 
                if (current == 0) {
                    JOptionPane.showMessageDialog(this, "Cannot divide by zero");
                    return;
                }
                result = prev / current; 
                break;
        }
        
        String historyItem = formatNum(prev) + " " + calcOperation + " " + formatNum(current) + " = " + formatNum(result);
        calcHistory.add(0, historyItem);
        if (calcHistory.size() > 5) calcHistory.remove(calcHistory.size() - 1);
        renderCalcHistory();
        
        calcDisplay = formatNum(result);
        calcPrevious = "";
        calcOperation = null;
        calcResetNext = true;
        updateCalcDisplay();
    }
    
    private void clearCalc() {
        calcDisplay = "0";
        calcPrevious = "";
        calcOperation = null;
        calcResetNext = false;
        updateCalcDisplay();
    }
    
    private void deleteLast() {
        if (calcDisplay.length() > 1) {
            calcDisplay = calcDisplay.substring(0, calcDisplay.length() - 1);
        } else {
            calcDisplay = "0";
        }
        updateCalcDisplay();
    }
    
    private void updateCalcDisplay() {
        calcDisplayLabel.setText(calcDisplay);
        calcPreviousLabel.setText(calcPrevious);
    }
    
    private void renderCalcHistory() {
        calcHistoryPanel.removeAll();
        
        if (calcHistory.isEmpty()) {
            showEmptyCalcHistory();
            return;
        }
        
        for (String item : calcHistory) {
            JLabel label = new JLabel(item);
            label.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
            label.setForeground(new Color(148, 163, 184));
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            calcHistoryPanel.add(label);
            calcHistoryPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        calcHistoryPanel.revalidate();
        calcHistoryPanel.repaint();
    }
    
    private void showEmptyCalcHistory() {
        calcHistoryPanel.removeAll();
        JLabel empty = new JLabel("No calculations yet");
        empty.setFont(new Font("SansSerif", Font.ITALIC, 12));
        empty.setForeground(new Color(71, 85, 105));
        empty.setAlignmentX(Component.CENTER_ALIGNMENT);
        calcHistoryPanel.add(empty);
        calcHistoryPanel.revalidate();
        calcHistoryPanel.repaint();
    }
    
    private void clearCalcHistory() {
        calcHistory.clear();
        renderCalcHistory();
    }
    
    // ==================== KEYBOARD SUPPORT ====================
    
    private void setupKeyboardListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() != KeyEvent.KEY_PRESSED) return false;
            
            Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (focusOwner instanceof JTextField) return false;
            
            char key = e.getKeyChar();
            String keyStr = String.valueOf(key);
            
            if (Character.isDigit(key)) {
                appendNumber(keyStr);
                return true;
            }
            if (key == '.') {
                appendNumber(".");
                return true;
            }
            if (key == '+' || key == '-' || key == '*' || key == '/') {
                appendOperator(String.valueOf(key));
                return true;
            }
            if (key == '=' || e.getKeyCode() == KeyEvent.VK_ENTER) {
                calculate();
                return true;
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                clearCalc();
                return true;
            }
            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                deleteLast();
                return true;
            }
            
            return false;
        });
    }
    
    // ==================== MAIN ====================
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            DualCalculator calc = new DualCalculator();
            calc.setVisible(true);
        });
    }
}

// Helper class for chain items
class ChainItem {
    double base;
    double percent;
    String op;
    double result;
    
    ChainItem(double base, double percent, String op, double result) {
        this.base = base;
        this.percent = percent;
        this.op = op;
        this.result = result;
    }
}
