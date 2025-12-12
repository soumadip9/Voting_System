import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ElectionDAO {

    public Map<Integer, String> getAllElections() {
        Map<Integer, String> elections = new HashMap<>();
        String sql = "SELECT Election_id, Title FROM election";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                elections.put(rs.getInt("Election_id"), rs.getString("Title"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return elections;
    }

    public Map<Integer, String> getCandidatesForElection(int electionId) {
        Map<Integer, String> candidates = new HashMap<>();
        String sql = "SELECT Candidate_id, Name, Party FROM candidate WHERE Election_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, electionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("Candidate_id");
                String name = rs.getString("Name") + " (" + rs.getString("Party") + ")";
                candidates.put(id, name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidates;
    }

    public boolean isElectionOngoing(int electionId) {
        String query = "SELECT End_date FROM election WHERE Election_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, electionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                java.sql.Date endDate = rs.getDate("End_date");
                return endDate != null && endDate.after(new java.util.Date());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Example method inside ElectionDAO
    public Map<Integer, Integer> getElectionResultsByCandidateId(int electionId) {
        Map<Integer, Integer> results = new HashMap<>();
        String query = "SELECT Candidate_id, Total_votes FROM election_result WHERE Election_id = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, electionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int candidateId = rs.getInt("Candidate_id");
                int totalVotes = rs.getInt("Total_votes");
                results.put(candidateId, totalVotes);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return results;
    }


    public Map<String, Integer> getElectionResults(int electionId) {
        Map<String, Integer> results = new HashMap<>();
        String query = """
            SELECT c.Name, COUNT(v.Vote_id) as VoteCount
            FROM candidate c
            LEFT JOIN votes v ON c.Candidate_id = v.Candidate_id AND v.Election_id = ?
            WHERE c.Election_id = ?
            GROUP BY c.Name
            ORDER BY VoteCount DESC
        """;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, electionId);
            stmt.setInt(2, electionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.put(rs.getString("Name"), rs.getInt("VoteCount"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public int getElectionIdByTitle(String title) {
        String sql = "SELECT Election_id FROM elections WHERE Title = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Election_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // invalid
    }
}
