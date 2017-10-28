/*
 * File:    DatabaseAccess.java
 * Package: database
 * Author:  Zachary Gill
 */

package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides the server access to the MySQL database.
 */
public final class DatabaseAccess
{
    
    //Constants
    
    /**
     * The maximum number of rows that can be returned from an SQL query.
     */
    public static final int MAX_ROWS = 50000000;
    
    
    //Static Fields
    
    /**
     * A flag indicating whether setup has occurred.
     */
    private static final AtomicBoolean setup = new AtomicBoolean(false);
    
    /**
     * The connections to the MySQL database.
     */
    private static Connection connection = null;
    
    /**
     * A list of statements opened by in the MySQL database.
     */
    private static final List<Statement> statements = new ArrayList<>();
    
    /**
     * A thread used to keep the connection to the MySQL database alive.
     */
    private static Timer keepAlive = null;
    
    
    //Functions
    
    /**
     * Performs setup operations for the DBMS.
     *
     * @return Whether the setup was successful or not.
     */
    public static boolean setup()
    {
        if (!setup.get()) {
    
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cop4331?autoReconnect=true&serverTimezone=UTC", "root", "root");
        
            } catch (ClassNotFoundException | SQLException e) {
                return false;
            }
    
            keepAlive = new Timer("KeepAlive");
            keepAlive.scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run()
                {
                    try {
                        PreparedStatement s = getPreparedStatement("SELECT * FROM user WHERE name = ?");
                        if (s == null) {
                            return;
                        }
                        s.setString(1, "cop4331");
                
                        executeSql(s);
                        closeStatement(s);
                
                    } catch (Exception ignored) {
                    }
                }
            }, 0, 60000);
            
            setup.set(true);
            return true;
        }
        return false;
    }
    
    /**
     * Performs shutdown operations for the DBMS.
     *
     * @return Whether the shutdown was successful or not.
     */
    public static boolean shutdown()
    {
        if (setup.get()) {
    
            if (keepAlive != null) {
                keepAlive.cancel();
            }
            
            try {
                connection.close();
            } catch (SQLException ignored) {
                //this should always throw an exception
            }
            
            try {
                for (Statement statement : statements) {
                    if (!statement.isClosed()) {
                        statement.close();
                    }
                }
            } catch (SQLException e) {
                return false;
            }
            
            setup.set(false);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Commits changes queued in the connection.
     *
     * @return Whether the changes were successfully committed or not.
     */
    public static boolean commitChanges()
    {
        if (!setup.get()) {
            return false;
        }
        
        try {
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Rolls back changes queued in the connection.
     *
     * @return Whether the changes were successfully rolled back or not.
     */
    public static boolean rollbackChanges()
    {
        if (!setup.get()) {
            return false;
        }
        
        try {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Sets the connection's read only value.
     *
     * @param readOnly The new read only value.
     * @return Whether the connection's read only value was successfully set or not.
     */
    public static boolean setReadOnly(boolean readOnly)
    {
        if (!setup.get()) {
            return false;
        }
        
        try {
            connection.setReadOnly(readOnly);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Determines if the connection is set to read only or not.
     *
     * @return Whether the connection is set to read only or not.
     */
    public static boolean isReadOnly()
    {
        if (!setup.get()) {
            return false;
        }
        
        try {
            return connection.isReadOnly();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Creates a statement on the connection.
     *
     * @return The statement created on the connection or null if the statement could not be created.
     */
    public static Statement getStatement()
    {
        if (!setup.get()) {
            return null;
        }
        
        Statement s;
        try {
            s = connection.createStatement();
            s.setMaxRows(MAX_ROWS);
            statements.add(s);
        } catch (SQLException e) {
            return null;
        }
        return s;
    }
    
    /**
     * Creates a prepared statement on the connection.
     *
     * @param sql The SQL statement to prepare the statement with.
     * @return The prepared statement created on the connection or null if the prepared statement could not be created.
     */
    public static PreparedStatement getPreparedStatement(String sql)
    {
        if (!setup.get()) {
            return null;
        }
        
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement(sql);
            ps.setMaxRows(MAX_ROWS);
            statements.add(ps);
        } catch (SQLException e) {
            return null;
        }
        return ps;
    }
    
    /**
     * Creates a callable statement on the connection.
     *
     * @param sql The SQL statement to prepare the statement with.
     * @return The callable statement created on the connection or null if the callable statement could not be created.
     */
    public static CallableStatement getCallableStatement(String sql)
    {
        if (!setup.get()) {
            return null;
        }
        
        CallableStatement cs;
        try {
            cs = connection.prepareCall(sql);
            cs.setMaxRows(MAX_ROWS);
            statements.add(cs);
        } catch (SQLException e) {
            return null;
        }
        return cs;
    }
    
    /**
     * Closes a statement on the connection.
     *
     * @param s The statement to close.
     * @return Whether the statement was successfully closed or not.
     */
    public static boolean closeStatement(Statement s)
    {
        if (!setup.get()) {
            return false;
        }
        
        try {
            s.close();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Executes an SQL statement.
     *
     * @param s   The statement to execute the SQL statement with.
     * @param sql The SQL statement.
     * @return Whether the SQL statement was successfully executed or not.
     */
    public static boolean executeSql(Statement s, String sql)
    {
        if (!setup.get()) {
            return false;
        }
        
        try {
            if ((sql == null) && (s instanceof PreparedStatement)) {
                ((PreparedStatement) s).execute();
            } else {
                s.execute(sql);
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Executes an SQL statement.
     *
     * @param s The statement to execute.
     * @return Whether the SQL statement was successfully executed or not.
     */
    public static boolean executeSql(Statement s)
    {
        return executeSql(s, null);
    }
    
    /**
     * Executes an SQL query statement.<br>
     * The returned ResultSet must be closed manually.
     *
     * @param s   The statement to execute the SQL query statement with.
     * @param sql The SQL query statement.
     * @return The result set of the SQL query statement.
     */
    @SuppressWarnings("JDBCResourceOpenedButNotSafelyClosed")
    public static ResultSet querySql(Statement s, String sql)
    {
        if (!setup.get()) {
            return null;
        }
        
        ResultSet rs;
        try {
            //noinspection IfMayBeConditional
            if ((sql == null) && (s instanceof PreparedStatement)) {
                rs = ((PreparedStatement) s).executeQuery();
            } else {
                rs = s.executeQuery(sql);
            }
        } catch (SQLException e) {
            return null;
        }
        return rs;
    }
    
    /**
     * Executes an SQL query statement.<br>
     * The returned ResultSet must be closed manually.
     *
     * @param s The statement to execute.
     * @return The result set of the SQL query statement.
     */
    public static ResultSet querySql(Statement s)
    {
        return querySql(s, null);
    }
    
    /**
     * Executes an SQL query statement and returns a formatted response with the column name as the key and the list of row values as the value.
     *
     * @param s   The statement to execute the SQL query statement with.
     * @param sql The SQL query statement.
     * @return The result set of the SQL query statement as a formatted response with the column name as the key and the list of row values as the value.
     */
    public static FormattedResultSet querySqlFormatResponse(Statement s, String sql)
    {
        ResultSet rs = querySql(s, sql);
        return formatResultSet(rs);
    }
    
    /**
     * Executes an SQL query statement and returns a formatted response with the column name as the key and the list of row values as the value.
     *
     * @param s The statement to execute.
     * @return The result set of the SQL query statement as a formatted response with the column name as the key and the list of row values as the value.
     */
    public static FormattedResultSet querySqlFormatResponse(Statement s)
    {
        return querySqlFormatResponse(s, null);
    }
    
    /**
     * Formats a result set into a map with the column name as the key and the list of row values as the value.
     *
     * @param rs The result set to format.
     * @return A formatted result set map with the column name as the key and the list of row values as the value.
     */
    public static FormattedResultSet formatResultSet(ResultSet rs)
    {
        return new FormattedResultSet(rs);
    }
    
    /**
     * Executes an SQL update statement.
     *
     * @param s   The statement to execute the SQL update statement with.
     * @param sql The SQL update statement.
     * @return The number of rows updated by the SQL query statement.
     */
    public static int updateSql(Statement s, String sql)
    {
        if (!setup.get()) {
            return 0;
        }
        
        try {
            //noinspection IfMayBeConditional
            if ((sql == null) && (s instanceof PreparedStatement)) {
                return ((PreparedStatement) s).executeUpdate();
            } else {
                return s.executeUpdate(sql);
            }
        } catch (SQLException e) {
            return -1;
        }
    }
    
    /**
     * Executes an SQL update statement.
     *
     * @param s The statement to execute.
     * @return The number of rows updated by the SQL query statement.
     */
    public static int updateSql(Statement s)
    {
        return updateSql(s, null);
    }
    
}
