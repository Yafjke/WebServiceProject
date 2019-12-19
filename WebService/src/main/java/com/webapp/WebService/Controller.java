package com.webapp.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.PrintStream;
import java.nio.file.FileStore;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")

public class Controller {

    static final String DB_URL = "jdbc:postgresql://localhost:5432/GeoData";
    static final String USER = "postgres";
    static final String PASS = "postgres";
	static Connection connection = null;
	static ResultSet resultSet = null;
	static Statement statement = null;
	static final String GET_QUERRY = "SELECT points.id, ST_AsText(points.geom) FROM databank.points;";
	static final String POST_QUERRY = "call databank.addpoint(?::geometry);";
    
	static Integer id = null;
	static String geometry = null;
	
	public static void ConnectToPostgres()
	{
		connection = null;
    	try {
    		connection = DriverManager.getConnection(DB_URL, USER, PASS);
    		Class.forName("org.postgresql.Driver");
    		System.out.println("Connected to PostgreSQL database!");
    		statement = connection.createStatement();
    	} catch (ClassNotFoundException e) {
    		System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
    		e.printStackTrace();
    	} catch (SQLException e) {
    		System.err.println("Connection failure");
			e.printStackTrace();
		}
	}
	
	@GetMapping("/getdata")
	public ArrayList<GeometryObject> GetData()
	{
		ConnectToPostgres();
		ArrayList<GeometryObject> list = new ArrayList<GeometryObject>();
		try {
			resultSet = statement.executeQuery(GET_QUERRY);
			while(resultSet.next()) {
				id = resultSet.getInt("id");
				geometry = resultSet.getString("ST_AsText");
				list.add(new GeometryObject(id, geometry));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@PostMapping("/upload") // used to be /upload/{geomtype} and ,@PathVariable String geomtype
	/*public ResponseEntity<List<GeometryObject>> PostData(@RequestBody List<GeometryObject> geom) {
		ConnectToPostgres();
		//geom.stream().forEach(System.out.println()); // g for geom i dunno saw it in tutorial
		for(GeometryObject id : geom)
		{
		}
		return new ResponseEntity<List<GeometryObject>>(geom, HttpStatus.OK);
	}*/
	public void PostData (@RequestParam(name = "geom") String insertedGeom) {
		ConnectToPostgres();
		try{
			CallableStatement postStatement = connection.prepareCall(POST_QUERRY);
			postStatement.setString(1, insertedGeom);
			postStatement.execute();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}