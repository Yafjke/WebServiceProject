package com.webapp.WebService;

import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

@RestController
@RequestMapping("/payment")

public class Controller {
	
    static final String DB_URL = "jdbc:postgresql://localhost:5432/GeoData";
    static final String USER = "postgres";
    static final String PASS = "postgres";
	static Connection connection = null;
	static ResultSet resultSet = null;
	static Statement statement = null;
	static final String QUERRY = "SELECT points.id, ST_AsText(points.geom) FROM databank.points;";
    
	static Integer id = null;
	static String geometry = null;
	
	private final String sharedKey = "SHARED_KEY";
	
	private static final String SUCCESS_STATUS = "success";
	private static final String ERROR_STATUS = "error";
	private static final Integer CODE_SUCCESS = 100;
	private static final Integer AUTH_FAILURE = 102;
	
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
	/*public BaseResponse showStatus()
	{
		
		return new BaseResponse(SUCCESS_STATUS, 1);
	}*/
	public ArrayList<GeometryObject> GetData()
	{
		ConnectToPostgres();
		ArrayList<GeometryObject> list = new ArrayList<GeometryObject>();
		try {
			resultSet = statement.executeQuery(QUERRY);
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
	/*@PostMapping("/pay")
    public BaseResponse pay(@RequestParam(value = "key") String key, @RequestBody Method request) {

        final BaseResponse response;

        if (sharedKey.equalsIgnoreCase(key)) {
            int userId = request.getUserId();
            String itemId = request.getItemId();
            double discount = request.getDiscount();
            // Process the request
            // ....
            // Return success response to the client.
            response = new BaseResponse(SUCCESS_STATUS, CODE_SUCCESS);
        } else {
            response = new BaseResponse(ERROR_STATUS, AUTH_FAILURE);
        }
        return response;
    }*/
}