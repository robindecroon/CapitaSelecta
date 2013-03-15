package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.sqlite.SQLiteConfig;

public class Database {
	
	private static String DB_NAME = "jdbc:sqlite:sample.db;";
	
	private static final String TABLE_PAPERS = "papers";
	private static final String PAPERS_ID = "paper_id";
    private static final String PAPERS_TITLE = "title";
    private static final String PAPERS_YEAR = "year";
    private static final String PAPERS_CONFERENCE = "conference";
    private static final String PAPERS_FULL_TEXT = "full_text";
    
    private static final String TABLE_AUTHORS = "authors";
    private static final String AUTHORS_ID = "author_id";
    private static final String AUTHORS_FIRST_NAME = "first_name";
    private static final String AUTHORS_LAST_NAME = "last_name";
    private static final String AUTHORS_UNIVERSITY_ID = "university_id";
    private static final String AUTHORS_COUNTRY_ABBR = "country_abbr";
    
    private static final String TABLE_PAPERS_AUTHORS = "papers_authors";
    private static final String PAPERS_AUTHORS_AUTHOR_ID= "author_id";
    private static final String PAPERS_AUTHORS_PAPER_ID= "paper_id";
    
    private static final String TABLE_COUNTRIES = "countries";
    private static final String COUNTRIES_ABBREVIATION= "abbreviation";
    private static final String COUNTRIES_NAME= "name";
    private static final String COUNTRIES_X_COORD= "country_x_coord";
    private static final String COUNTRIES_Y_COORD= "country_y_coord";
    
    private static final String TABLE_UNIVERSITIES = "universities";
    private static final String UNIVERSITIES_ID= "university_id";
    private static final String UNIVERSITIES_NAME= "university_name";
    private static final String UNIVERSITIES_COUNTRY_ABBR = "country_abbr";
    private static final String UNIVERSITIES_X_COORD= "university_x_coord";
    private static final String UNIVERSITIES_Y_COORD= "university_y_coord";
	
	public static void main(String[] args) throws ClassNotFoundException {
	    Class.forName("org.sqlite.JDBC");
	    Connection connection = null;
	    
	    try {
	    		SQLiteConfig config = new SQLiteConfig();
	    		config.enforceForeignKeys(true);
	    	
		    connection = DriverManager.getConnection(DB_NAME);
		    Statement statement = connection.createStatement();
		    statement.setQueryTimeout(30);
		    statement.execute("PRAGMA foreign_keys=ON;");
		      
		    createDatabaseTable();
		    
		    SQLPaper paper = new SQLPaper("Titelken", 2004, "Conferenceken", "Dit is de full text.");
		    SQLPaper paper1 = new SQLPaper("Titelken2", 2006, "Conferenceken", "Dit is de full text2.");
		    SQLPaper paper2 = new SQLPaper("Titelken3", 2004, "Conferenceken3", "Dit is de full text3.");
		    insertPaper(paper);
		    insertPaper(paper1);
		    insertPaper(paper2);
		    
		    getAllPapers();
		    getAllPapersForYear(2004);
		    getAllPapersForConference(new SQLConference("Conferenceken"));
		    getPapersForId(1);
	    } catch(SQLException e) {
	    		System.err.println(e.getMessage());
	    } finally {
	    		try {
	    			if(connection != null)
	    				connection.close();
	    		} catch(SQLException e) {
	    			System.err.println(e);
	    		}
	    }
	}

	private static void createDatabaseTable() {
		Connection connection = null;
		System.out.println("Creating");
		
		try { 
			connection = DriverManager.getConnection(DB_NAME);
			Statement statement = connection.createStatement();
			
			String CREATE_PAPERS_TABLE =  "CREATE TABLE IF NOT EXISTS " + TABLE_PAPERS + " (" 
					+ PAPERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
					+ PAPERS_TITLE + " TEXT NOT NULL, "
					+ PAPERS_YEAR + " INTEGER NOT NULL, "
					+ PAPERS_CONFERENCE + " TEXT NOT NULL, "
					+ PAPERS_FULL_TEXT + " TEXT NOT NULL"
					+ ")";
			
			String CREATE_AUTHORS_TABLE =  "CREATE TABLE IF NOT EXISTS " + TABLE_AUTHORS + " (" 
					+ AUTHORS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
					+ AUTHORS_FIRST_NAME + " TEXT NOT NULL, "
					+ AUTHORS_LAST_NAME + " TEXT NOT NULL, "
					+ AUTHORS_COUNTRY_ABBR + " TEXT NOT NULL , "
					+ AUTHORS_UNIVERSITY_ID + " INTEGER NOT NULL , "
					+ "FOREIGN KEY(" + AUTHORS_COUNTRY_ABBR + ") REFERENCES " + TABLE_COUNTRIES +"(" + COUNTRIES_ABBREVIATION + "), "
					+ "FOREIGN KEY(" + AUTHORS_UNIVERSITY_ID + ") REFERENCES " + TABLE_UNIVERSITIES +"(" + UNIVERSITIES_ID + ")"
					+ ")";
	 	
			String CREATE_PAPERS_AUTHORS_TABLE =  "CREATE TABLE IF NOT EXISTS " + TABLE_PAPERS_AUTHORS + " (" 
					+ PAPERS_AUTHORS_PAPER_ID +  " INTEGER NOT NULL, "
					+ PAPERS_AUTHORS_AUTHOR_ID + " INTEGER NOT NULL , "
					+ "PRIMARY KEY (" + PAPERS_AUTHORS_PAPER_ID + ", " + PAPERS_AUTHORS_AUTHOR_ID + "), "
					+ "FOREIGN KEY(" + PAPERS_AUTHORS_PAPER_ID + ") REFERENCES " + TABLE_PAPERS +"(" + PAPERS_ID + "), "
					+ "FOREIGN KEY(" + PAPERS_AUTHORS_AUTHOR_ID + ") REFERENCES " + TABLE_AUTHORS +"(" + AUTHORS_ID + ")"
					+ ")";
			
			String CREATE_COUNTRIES_TABLE =  "CREATE TABLE IF NOT EXISTS " + TABLE_COUNTRIES + " (" 
					+ COUNTRIES_ABBREVIATION + " TEXT PRIMARY KEY, "
					+ COUNTRIES_NAME + " TEXT NOT NULL, "
					+ COUNTRIES_X_COORD + " FLOAT NOT NULL, "
					+ COUNTRIES_Y_COORD + " FLOAT NOT NULL "
					+ ")";
			
			String CREATE_UNIVERSITIES_TABLE =  "CREATE TABLE IF NOT EXISTS " + TABLE_UNIVERSITIES + " (" 
					+ UNIVERSITIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ UNIVERSITIES_COUNTRY_ABBR + " TEXT NOT NULL , "
					+ UNIVERSITIES_NAME + " TEXT NOT NULL, "
					+ UNIVERSITIES_X_COORD + " FLOAT NOT NULL, "
					+ UNIVERSITIES_Y_COORD + " FLOAT NOT NULL, "
					+ "FOREIGN KEY(" + UNIVERSITIES_COUNTRY_ABBR + ") REFERENCES " +TABLE_COUNTRIES +"(" + COUNTRIES_ABBREVIATION + ")"
					+ ")";
	
			statement.executeUpdate(CREATE_PAPERS_TABLE);
			statement.executeUpdate(CREATE_AUTHORS_TABLE);
			statement.executeUpdate(CREATE_COUNTRIES_TABLE);
			statement.executeUpdate(CREATE_UNIVERSITIES_TABLE);
			statement.executeUpdate(CREATE_PAPERS_AUTHORS_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void insertPaper(SQLPaper paper) {
		Connection connection = null;
		
		try {
			connection = DriverManager.getConnection(DB_NAME);
			Statement statement = connection.createStatement();
			
			String INSERT_PAPER_QUERY = "INSERT INTO " 
					+ TABLE_PAPERS
					+ " (" + PAPERS_TITLE + ", " + PAPERS_YEAR + ", " + PAPERS_CONFERENCE + ", " + PAPERS_FULL_TEXT + ") "
					+ "values( '"+ paper.getTitle() +"', " + paper.getYear() + ", '" + paper.getConference() + "', '" + paper.getFullText() + "')";
			
			statement.execute(INSERT_PAPER_QUERY);
		} catch (SQLException e) {
			System.out.println("InsertPaper: Kan paper niet toevoegen.");
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static ArrayList<SQLPaper> getAllPapers() {
		Connection connection = null;
		ArrayList<SQLPaper> papers = new ArrayList<SQLPaper>();
		
		try {
			connection = DriverManager.getConnection(DB_NAME);
			Statement statement = connection.createStatement();
			
			String SELECT_PAPERS_QUERY =	"SELECT *" 
																+ " FROM " + TABLE_PAPERS;
			
			ResultSet rs = statement.executeQuery(SELECT_PAPERS_QUERY);
		    	while(rs.next()) {
		    		String title = rs.getString(PAPERS_TITLE);
		    		int theYear = rs.getInt(PAPERS_YEAR);
		    		String conference = rs.getString(PAPERS_CONFERENCE);
		    		String fullText = rs.getString(PAPERS_FULL_TEXT);
		    		SQLPaper paper = new SQLPaper(title, theYear, conference, fullText);
		    		papers.add(paper);
		    		System.out.println(paper.getTitle());
		    	}
		} catch (SQLException e) {
			System.out.println("getAllPapers: Kan papers voor het jaar niet ophalen.");
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return papers;
	}
	
	public static ArrayList<SQLPaper> getPapersForId(int id) {
		Connection connection = null;
		ArrayList<SQLPaper> papers = new ArrayList<SQLPaper>();
		
		try {
			connection = DriverManager.getConnection(DB_NAME);
			Statement statement = connection.createStatement();
			
			String SELECT_PAPERS_QUERY =	"SELECT *" 
																+ " FROM " + TABLE_PAPERS
																+ " WHERE " + PAPERS_ID + "=" + id;
			
			ResultSet rs = statement.executeQuery(SELECT_PAPERS_QUERY);
		    	while(rs.next()) {
		    		String title = rs.getString(PAPERS_TITLE);
		    		int year = rs.getInt(PAPERS_YEAR);
		    		String conference = rs.getString(PAPERS_CONFERENCE);
		    		String fullText = rs.getString(PAPERS_FULL_TEXT);
		    		SQLPaper paper = new SQLPaper(title, year, conference, fullText);
		    		papers.add(paper);
		    		System.out.println(paper.getTitle());
		    	}
		} catch (SQLException e) {
			System.out.println("getPaperForId: Kan paper voor het id " + id + " niet ophalen.");
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return papers;
	}
	
	public static ArrayList<SQLPaper> getAllPapersForYear(int year) {
		Connection connection = null;
		ArrayList<SQLPaper> papers = new ArrayList<SQLPaper>();
		
		try {
			connection = DriverManager.getConnection(DB_NAME);
			Statement statement = connection.createStatement();
			
			String SELECT_PAPERS_QUERY =	"SELECT *" 
																+ " FROM " + TABLE_PAPERS
																+ " WHERE " + PAPERS_YEAR + "=" + year;
			
			ResultSet rs = statement.executeQuery(SELECT_PAPERS_QUERY);
		    	while(rs.next()) {
		    		String title = rs.getString(PAPERS_TITLE);
		    		int theYear = rs.getInt(PAPERS_YEAR);
		    		String conference = rs.getString(PAPERS_CONFERENCE);
		    		String fullText = rs.getString(PAPERS_FULL_TEXT);
		    		SQLPaper paper = new SQLPaper(title, theYear, conference, fullText);
		    		papers.add(paper);
		    		System.out.println(paper.getTitle());
		    	}
		} catch (SQLException e) {
			System.out.println("getAllPapersForYear: Kan papers voor het jaar " + year + " niet ophalen.");
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return papers;
	}
	
	public static ArrayList<SQLPaper> getAllPapersForConference(SQLConference conference) {
		Connection connection = null;
		ArrayList<SQLPaper> papers = new ArrayList<SQLPaper>();
		
		try {
			connection = DriverManager.getConnection(DB_NAME);
			Statement statement = connection.createStatement();
			
			String SELECT_PAPERS_QUERY =	"SELECT *" 
																+ " FROM " + TABLE_PAPERS
																+ " WHERE " + PAPERS_CONFERENCE + "='" + conference.getName() + "'";
			
			ResultSet rs = statement.executeQuery(SELECT_PAPERS_QUERY);
		    	while(rs.next()) {
		    		String title = rs.getString(PAPERS_TITLE);
		    		int year = rs.getInt(PAPERS_YEAR);
		    		String theConference = rs.getString(PAPERS_CONFERENCE);
		    		String fullText = rs.getString(PAPERS_FULL_TEXT);
		    		SQLPaper paper = new SQLPaper(title, year, theConference, fullText);
		    		papers.add(paper);
		    		System.out.println(paper.getTitle());
		    	}
		} catch (SQLException e) {
			System.out.println("getAllPapersForConference: Kan papers voor de conference " + conference + " niet ophalen.");
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return papers;
	}

}
