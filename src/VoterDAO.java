import java.sql.*;

public class VoterDAO {
    public static boolean registerVoter(String name, String email, String password) {
        String sql = "INSERT INTO voters (Name, Email, Password, has_voted) VALUES (?, ?, ?, 0)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
        return false;
    }
    public String getVoterNameById(int voterId) {
        String name = null;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT Name FROM voters WHERE Voters_id = ?")) {
            stmt.setInt(1, voterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("Name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public boolean login(String email, String password) {
        String sql = "SELECT * FROM voters WHERE Email = ? AND Password = ?";
        boolean success = false;
        int voterId = -1;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                success = true;
                voterId = rs.getInt("Voters_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Log login attempt
        logLoginAttempt(email, success);

        return success;
    }

    private void logLoginAttempt(String email, boolean success) {
        String fetchVoterIdSQL = "SELECT Voters_id FROM voters WHERE Email = ?";
        String insertLogSQL = "INSERT INTO Voter_Auth_Log (Voters_id, Login_time, Ip_address, Status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement fetchStmt = conn.prepareStatement(fetchVoterIdSQL)) {

            fetchStmt.setString(1, email);
            ResultSet rs = fetchStmt.executeQuery();
            if (rs.next()) {
                int voterId = rs.getInt("Voters_id");

                try (PreparedStatement insertStmt = conn.prepareStatement(insertLogSQL)) {
                    insertStmt.setInt(1, voterId);
                    insertStmt.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
                    insertStmt.setString(3, Utils.generateRandomIP());
                    insertStmt.setString(4, success ? "success" : "failed");
                    insertStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public boolean hasVotedInElection(int voterId, int electionId) {
        String sql = "SELECT COUNT(*) FROM vote WHERE Voters_id = ? AND Election_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, voterId);
            ps.setInt(2, electionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    public boolean castVote(int voterId, int candidateId, int electionId) {
        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);

            // Step 1: Insert into vote table
            String voteSQL = "INSERT INTO vote (Voters_id, candidate_id, election_id) VALUES (?, ?, ?)";
            try (PreparedStatement voteStmt = conn.prepareStatement(voteSQL)) {
                voteStmt.setInt(1, voterId);
                voteStmt.setInt(2, candidateId);
                voteStmt.setInt(3, electionId);
                voteStmt.executeUpdate();
            }

            // Step 2: Mark voter as has_voted = 1
            String updateVoterSQL = "UPDATE voters SET has_voted = 1 WHERE Voters_id = ?";
            try (PreparedStatement voterStmt = conn.prepareStatement(updateVoterSQL)) {
                voterStmt.setInt(1, voterId);
                voterStmt.executeUpdate();
            }

            // Step 3: Check if record exists in Election_Result
            String selectResultSQL = "SELECT Total_votes FROM Election_Result WHERE Election_id = ? AND Candidate_id = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectResultSQL)) {
                selectStmt.setInt(1, electionId);
                selectStmt.setInt(2, candidateId);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    // Record exists → update
                    String updateResultSQL = "UPDATE Election_Result SET Total_votes = Total_votes + 1 WHERE Election_id = ? AND Candidate_id = ?";
                    try (PreparedStatement updateResultStmt = conn.prepareStatement(updateResultSQL)) {
                        updateResultStmt.setInt(1, electionId);
                        updateResultStmt.setInt(2, candidateId);
                        updateResultStmt.executeUpdate();
                    }
                } else {
                    // No record → insert new
                    String insertResultSQL = "INSERT INTO Election_Result (Election_id, Candidate_id, Total_votes) VALUES (?, ?, 1)";
                    try (PreparedStatement insertResultStmt = conn.prepareStatement(insertResultSQL)) {
                        insertResultStmt.setInt(1, electionId);
                        insertResultStmt.setInt(2, candidateId);
                        insertResultStmt.executeUpdate();
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }




    public Integer getVoterIdByEmail(String email) {
        String sql = "SELECT Voters_id FROM voters WHERE Email = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("Voters_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
