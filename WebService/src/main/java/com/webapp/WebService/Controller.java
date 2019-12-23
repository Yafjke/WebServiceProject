package com.webapp.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
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
@RequestMapping("/api")

public class Controller {

    static final String DB_URL = "jdbc:postgresql://localhost:5432/GeoData";
    static final String USER = "postgres";
    static final String PASS = "postgres";
	static Connection connection = null;
	static ResultSet resultSet = null;
	static Statement statement = null;
	static final String POINT = "POINT";
	static final String MULTIPOINT = "MULTIPOINT";
	static final String LINE = "LINESTRING";
	static final String MULTILINE = "MULTILINESTRING";
	static final String POLYGON = "POLYGON";
	static final String MULTIPOLYGON = "MULTIPOLYGON";
    
	static Integer id = null;
	static String geometry = null;

	public static String DataTypeStringCreator(String insertedGeom) {
		String geomtype = "";
		System.out.println("HEY NOW");
		if(insertedGeom.matches("MULTI(.*)")){
			geomtype += "multi";
			System.out.println(geomtype);
			if(insertedGeom.matches("(.*)POINT(.*)")) {
				geomtype +="points";
			}
			if(insertedGeom.matches("(.*)LINESTRING(.*)")) {
				geomtype += "lines";
			}
			if(insertedGeom.matches("(.*)POLYGON(.*)")) {
				geomtype += "polygons";
			}
		} else if(insertedGeom.matches("POINT(.*)")) {
			geomtype +="points";
		} else if(insertedGeom.matches("LINESTRING(.*)")) {
			geomtype += "lines";
		} else if(insertedGeom.matches("POLYGON(.*)")) {
			geomtype += "polygons";
		}
		return geomtype;
	}
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
	private static void CallToInformStart() {
		try {
		final String uri = "https://someshot.com/api/start";
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(uri, String.class);
		System.out.println(result);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	private static void CallToInformEnd() {
		try {
		final String uri = "https://someshot.com/api/end";
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(uri, String.class);
		System.out.println(result);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("/getdata/{geomtype}")
	public ArrayList<GeometryObject> GetData(@PathVariable String geomtype)
	{
		CallToInformStart();
		ConnectToPostgres();
		ArrayList<GeometryObject> list = new ArrayList<GeometryObject>();
		try {
			String GET_QUERY = "SELECT " + geomtype + ".id, ST_AsText(" + geomtype + ".geom) FROM databank." 
			+ geomtype +";";
			PreparedStatement getStatement = connection.prepareStatement(GET_QUERY);
			resultSet = getStatement.executeQuery();
			while(resultSet.next()) {
				id = resultSet.getInt("id");
				geometry = resultSet.getString("ST_AsText");
				list.add(new GeometryObject(id, geometry));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		CallToInformEnd();
		return list;
	}
	@PostMapping("/upload")
	public void PostData (@RequestParam(name = "geom") String insertedGeom) {
		CallToInformStart();
		ConnectToPostgres();
		String geomtype = DataTypeStringCreator(insertedGeom);
		System.out.println(geomtype);
		try{
			String POST_QUERY = "call databank.add" + geomtype + "('" + insertedGeom + "'::geometry);";
			System.out.println(POST_QUERY);
			CallableStatement postStatement = connection.prepareCall(POST_QUERY);
			postStatement.execute();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		CallToInformEnd();
	}
}