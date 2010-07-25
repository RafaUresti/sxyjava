import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import oracle.sql.*;

public class BookAuthors {
	static String URL = "jdbc:oracle:thin:@//fling-l.seas.upenn.edu:1521/cisora";

	public static void main(String[] args){
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
		try {
			Connection conn = DriverManager.getConnection(URL, "xiaoyi", "oraclepassword");
			Statement s = conn.createStatement();
			String query = "SELECT A.NAME, A.AFFILIATION, COUNT(BA.ISBN) AS NUM " +
			"FROM Authors A LEFT OUTER JOIN BookAuthors BA " +
			"ON A.NAME = BA.AUTHOR " +
			"GROUP BY A.NAME, A.AFFILIATION";
			ResultSet rs = s.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();

			FileWriter fstream = new FileWriter("q2.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			
			
			for (int i = 0; i < rsmd.getColumnCount(); i ++)
				out.write(rsmd.getColumnName(i+1)+"\t");
			out.newLine();
			while (rs.next()){
				out.write(rs.getString("NAME")+"\t");
				out.write(rs.getString("AFFILIATION")+"\t");
				out.write(rs.getInt("NUM")+"");
				out.newLine();
			}
			out.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}
