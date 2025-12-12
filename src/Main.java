import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        VoterDAO voterDAO = new VoterDAO();
        ElectionDAO electionDAO = new ElectionDAO();

        System.out.println("Welcome to the Voting System!");
        System.out.println("1. Login");

        System.out.print("Choose an option: ");
        int choice = Integer.parseInt(sc.nextLine());

        String email = null;
        String password = null;
        Integer voterId = null;

        if (choice == 1) {
            // Login flow
            System.out.print("Enter Email: ");
            email = sc.nextLine();
            System.out.print("Enter Password: ");
            password = sc.nextLine();

            if (!voterDAO.login(email, password)) {
                System.out.println("Login failed.");
                return;
            }

            voterId = voterDAO.getVoterIdByEmail(email);
            if (voterId == null) {
                System.out.println("Voter not found.");
                return;
            }

        } else {
            System.out.println("Invalid choice.");
            return;
        }

        // Show available elections
        Map<Integer, String> elections = electionDAO.getAllElections();
        System.out.println("\nAvailable Elections:");
        for (Map.Entry<Integer, String> entry : elections.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue());
        }

        System.out.print("Enter Election ID: ");
        int electionId = Integer.parseInt(sc.nextLine());

        // Check if already voted in this election
        if (voterDAO.hasVotedInElection(voterId, electionId)) {
            System.out.println("You have already voted in this election.");
            return;
        }

        // Show candidates
        Map<Integer, String> candidates = electionDAO.getCandidatesForElection(electionId);
        if (candidates.isEmpty()) {
            System.out.println("No candidates found.");
            return;
        }

        System.out.println("\nCandidates:");
        for (Map.Entry<Integer, String> entry : candidates.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue());
        }

        System.out.print("Enter Candidate ID to vote for: ");
        int candidateId = Integer.parseInt(sc.nextLine());

        boolean success = voterDAO.castVote(voterId, candidateId, electionId);
        System.out.println(success ? "✅ Vote cast successfully!" : "❌ Failed to cast vote.");
    }
}

