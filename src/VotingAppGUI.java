import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class VotingAppGUI extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextPane messageArea;
    private JComboBox<String> electionCombo;
    private JComboBox<String> candidateCombo;
    private JButton loginButton, voteButton;

    private VoterDAO voterDAO = new VoterDAO();
    private ElectionDAO electionDAO = new ElectionDAO();
    private Integer currentVoterId = null;
    private Map<String, Integer> electionNameToId = new HashMap<>();
    private Map<String, Integer> candidateNameToId = new HashMap<>();

    public VotingAppGUI() {
        setTitle("eVoting System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));

        JLabel headerLabel = new JLabel("Welcome to eVoting System", JLabel.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(headerLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        add(contentPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField();
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 20));
        emailField.setPreferredSize(new Dimension(250, 40));
        contentPanel.add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 20));
        passwordField.setPreferredSize(new Dimension(250, 40));
        contentPanel.add(passwordField, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginButton = new JButton("Login");
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        contentPanel.add(loginButton, gbc);

        // Separator
        gbc.gridy = 3;
        JSeparator separator = new JSeparator();
        contentPanel.add(separator, gbc);

        // Elections
        gbc.gridwidth = 1;
        gbc.gridy = 4;
        gbc.gridx = 0;
        contentPanel.add(new JLabel("Elections:"), gbc);
        gbc.gridx = 1;
        electionCombo = new JComboBox<>();
        electionCombo.setEnabled(false);
        electionCombo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        contentPanel.add(electionCombo, gbc);

        // Candidates
        gbc.gridy = 5;
        gbc.gridx = 0;
        contentPanel.add(new JLabel("Candidates:"), gbc);
        gbc.gridx = 1;
        candidateCombo = new JComboBox<>();
        candidateCombo.setEnabled(false);
        candidateCombo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        contentPanel.add(candidateCombo, gbc);

        // Vote Button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        voteButton = new JButton("Vote");
        voteButton.setFocusPainted(false);
        voteButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        voteButton.setEnabled(false);
        contentPanel.add(voteButton, gbc);

        // Message Panel
        gbc.gridy = 7;
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        messagePanel.setPreferredSize(new Dimension(700, 120));

        StyledDocument doc = new DefaultStyledDocument();
        messageArea = new JTextPane(doc);
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        messageArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        messageArea.setPreferredSize(new Dimension(680, 100));

        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(null);
        messagePanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(messagePanel, gbc);

        // Listeners
        loginButton.addActionListener(new LoginHandler());
        electionCombo.addActionListener(e -> loadCandidates());
        voteButton.addActionListener(new VoteHandler());
    }

    private void loadElections() {
        electionCombo.removeAllItems();
        electionNameToId.clear();

        Map<Integer, String> elections = electionDAO.getAllElections();
        for (Map.Entry<Integer, String> entry : elections.entrySet()) {
            electionCombo.addItem(entry.getValue());
            electionNameToId.put(entry.getValue(), entry.getKey());
        }

        electionCombo.setEnabled(true);
        candidateCombo.setEnabled(false);
        voteButton.setEnabled(false);
    }

    private void loadCandidates() {
        String selectedElection = (String) electionCombo.getSelectedItem();
        if (selectedElection == null || currentVoterId == null) {
            candidateCombo.setEnabled(false);
            voteButton.setEnabled(false);
            return;
        }

        int electionId = electionNameToId.getOrDefault(selectedElection, -1);
        candidateCombo.removeAllItems();
        candidateNameToId.clear();

        boolean isOngoing = electionDAO.isElectionOngoing(electionId);

        if (!isOngoing) {
            voteButton.setEnabled(false);
            messageArea.setText("'" + selectedElection + "' has ended.\nElection Results:\n");

            Map<Integer, String> candidates = electionDAO.getCandidatesForElection(electionId);
            Map<Integer, Integer> results = electionDAO.getElectionResultsByCandidateId(electionId);

            int maxVotes = results.values().stream().mapToInt(v -> v).max().orElse(0);

            StringBuilder resultText = new StringBuilder("'" + selectedElection + "' has ended.\nElection Results:\n");
            for (Map.Entry<Integer, String> entry : candidates.entrySet()) {
                int candidateId = entry.getKey();
                String candidateName = entry.getValue();
                int votes = results.getOrDefault(candidateId, 0);
                String winnerMark = (votes == maxVotes && maxVotes > 0) ? " <- Winner" : "";
                resultText.append(candidateName).append(": ").append(votes).append(" votes").append(winnerMark).append("\n");
            }
            messageArea.setText(resultText.toString());
            candidateCombo.setEnabled(false);
            return;
        }

        boolean alreadyVoted = voterDAO.hasVotedInElection(currentVoterId, electionId);
        if (alreadyVoted) {
            voteButton.setEnabled(false);
            messageArea.setText("You have voted in '" + selectedElection + "'.");
            candidateCombo.setEnabled(false);
            return;
        }

        Map<Integer, String> candidates = electionDAO.getCandidatesForElection(electionId);
        for (Map.Entry<Integer, String> entry : candidates.entrySet()) {
            candidateCombo.addItem(entry.getValue());
            candidateNameToId.put(entry.getValue(), entry.getKey());
        }
        voteButton.setEnabled(true);
        candidateCombo.setEnabled(true);
        messageArea.setText("You may vote in '" + selectedElection + "'.");
    }

    class LoginHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (voterDAO.login(email, password)) {
                currentVoterId = voterDAO.getVoterIdByEmail(email);
                if (currentVoterId != null) {
                    String voterName = voterDAO.getVoterNameById(currentVoterId);
                    JOptionPane.showMessageDialog(null,
                            "You have successfully logged in\n" +
                                    "Name: " + voterName + "\n" +
                                    "Voter ID: " + currentVoterId);
                    messageArea.setText("Login successful.\nWelcome, " + voterName + "!\nVoter ID: " + currentVoterId + "\n");
                    loadElections();
                } else {
                    messageArea.setText("Login successful, but voter not found.");
                }
            } else {
                messageArea.setText("Login failed.");
            }
        }
    }

    class VoteHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (currentVoterId == null) {
                messageArea.setText("Please login first.");
                return;
            }

            String selectedElection = (String) electionCombo.getSelectedItem();
            String selectedCandidate = (String) candidateCombo.getSelectedItem();
            if (selectedElection == null || selectedCandidate == null) {
                messageArea.setText("Please select an election and candidate.");
                return;
            }

            int electionId = electionNameToId.getOrDefault(selectedElection, -1);
            int candidateId = candidateNameToId.getOrDefault(selectedCandidate, -1);

            boolean success = voterDAO.castVote(currentVoterId, candidateId, electionId);
            messageArea.setText(success ? "Vote cast successfully!" : "Vote failed.");
            loadCandidates();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VotingAppGUI().setVisible(true));
    }
}
