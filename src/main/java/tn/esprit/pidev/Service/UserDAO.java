package tn.esprit.pidev.Service;

import org.mindrot.jbcrypt.BCrypt;
import tn.esprit.pidev.Database.Database;
import tn.esprit.pidev.Model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection connection;
    private PreparedStatement pst;
    private ResultSet rs;

    public UserDAO() {
        connection = Database.getConnection();
    }

    /**
     * Hashes a password using BCrypt
     * @param plainPassword The plain text password to hash
     * @return The hashed password
     */
    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /**
     * Checks if a plain password matches a BCrypt hashed password
     * @param plainPassword The plain password to check
     * @param hashedPassword The hashed password to check against
     * @return True if the password matches, false otherwise
     */
    private boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    /**
     * Checks if an email exists in the database
     * @param email The email to check
     * @return True if the email exists, false otherwise
     */
    public boolean checkEmailExists(String email) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT COUNT(*) FROM user WHERE email = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, email);

            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * Updates user profile information
     * @param user The user object with updated information
     * @return True if update was successful, false otherwise
     */
    public boolean updateProfile(User user) {
        String query = "UPDATE user SET first_name = ?, last_name = ?, address = ?, phone_number = ?, specialite = ? WHERE id = ?";

        try {
            pst = connection.prepareStatement(query);
            pst.setString(1, user.getFirstName());
            pst.setString(2, user.getLastName());
            pst.setString(3, user.getAddress());
            pst.setString(4, user.getPhoneNumber());
            pst.setString(5, user.getSpecialite());
            pst.setInt(6, user.getId());

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error updating profile: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }



    /**
     * Updates a user's password in the database
     * @param email The user's email
     * @param newPassword The new password
     * @return True if the update was successful, false otherwise
     */
    public boolean updatePassword(String email, String newPassword) {
        PreparedStatement ps = null;

        try {
            String hashedPassword = hashPassword(newPassword);

            String query = "UPDATE user SET password = ? WHERE email = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, hashedPassword);
            ps.setString(2, email);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public boolean addUser(User user) {
        String query = "INSERT INTO user (email, password, first_name, last_name, role, specialite, address, birth_date, phone_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            String hashedPassword = hashPassword(user.getPassword());

            pst = connection.prepareStatement(query);
            pst.setString(1, user.getEmail());
            pst.setString(2, hashedPassword);
            pst.setString(3, user.getFirstName());
            pst.setString(4, user.getLastName());
            pst.setString(5, String.join(",", user.getRoles()));
            pst.setString(6, user.getSpecialite());
            pst.setString(7, user.getAddress());
            pst.setDate(8, user.getBirthDate());
            pst.setString(9, user.getPhoneNumber());

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error adding user: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    public User authenticateUser(String email, String password) {
        String query = "SELECT * FROM user WHERE email = ?";

        try {
            pst = connection.prepareStatement(query);
            pst.setString(1, email);

            rs = pst.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");

                if (checkPassword(password, storedHash)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(storedHash);
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));

                    String rolesStr = rs.getString("role");
                    user.setRoles(rolesStr != null ? rolesStr.split(",") : new String[0]);

                    user.setSpecialite(rs.getString("specialite"));
                    user.setAddress(rs.getString("address"));
                    user.setBirthDate(rs.getDate("birth_date"));
                    user.setPhoneNumber(rs.getString("phone_number"));
                    user.setPhoto(rs.getString("photo"));

                    User.connecte = user;
                    return user;
                }
            }
            return null;
        } catch (SQLException ex) {
            System.out.println("Error authenticating: " + ex.getMessage());
            return null;
        } finally {
            closeResources();
        }
    }

    public boolean updateUserPassword(int userId, String newPassword) {
        String query = "UPDATE user SET password = ? WHERE id = ?";

        try {
            String hashedPassword = hashPassword(newPassword);

            pst = connection.prepareStatement(query);
            pst.setString(1, hashedPassword);
            pst.setInt(2, userId);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error updating password: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";

        try {
            pst = connection.prepareStatement(query);
            pst.setString(1, email);

            rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException ex) {
            System.out.println("Error checking email: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    public List<User> getAllAdmin() {
        List<User> admins = new ArrayList<>();
        String query = "SELECT * FROM user WHERE role LIKE '%admin%'";

        try {
            pst = connection.prepareStatement(query);
            rs = pst.executeQuery();

            while (rs.next()) {
                User admin = new User();
                admin.setId(rs.getInt("id"));
                admin.setEmail(rs.getString("email"));
                admin.setFirstName(rs.getString("first_name"));
                admin.setLastName(rs.getString("last_name"));

                String rolesStr = rs.getString("role");
                admin.setRoles(rolesStr != null ? rolesStr.split(",") : new String[0]);

                admin.setSpecialite(rs.getString("specialite"));

                admins.add(admin);
            }
            return admins;
        } catch (SQLException ex) {
            System.out.println("Error getting admins: " + ex.getMessage());
            return admins;
        } finally {
            closeResources();
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";

        try {
            pst = connection.prepareStatement(query);
            rs = pst.executeQuery();

            while (rs.next()) {
                String rolesStr = rs.getString("role");
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        "",
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rolesStr != null ? rolesStr.split(",") : new String[0],
                        rs.getString("specialite"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("photo"),
                        rs.getString("address"),
                        rs.getDate("birth_date"),
                        rs.getString("phone_number")
                );
                users.add(user);
            }
        } catch (SQLException ex) {
            System.out.println("Error getting users: " + ex.getMessage());
        } finally {
            closeResources();
        }
        return users;
    }

    public boolean updateUser(User user) {
        String query = "UPDATE user SET first_name=?, last_name=?, role=?, phone_number=?, email=? WHERE id=?";

        try {
            pst = connection.prepareStatement(query);
            pst.setString(1, user.getFirstName());
            pst.setString(2, user.getLastName());
            pst.setString(3, String.join(",", user.getRoles()));
            pst.setString(4, user.getPhoneNumber());
            pst.setString(5, user.getEmail());
            pst.setInt(6, user.getId());

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error updating user: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    public boolean deleteUser(int id) {
        String query = "DELETE FROM user WHERE id=?";

        try {
            pst = connection.prepareStatement(query);
            pst.setInt(1, id);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error deleting user: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        } catch (SQLException ex) {
            System.out.println("Error closing resources: " + ex.getMessage());
        }
    }
}