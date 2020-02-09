package com.wipro.sdet;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import org.json.JSONObject;

public class Run {

	public static void main(String[] args){

		Scanner queryString = new Scanner(System.in);
		System.out.println("Enter your query: ");
		String query = queryString.nextLine();
		queryString.close();

		JSONObject resultSet = new QueryProcessor().processQuery(new QueryParser().parseQuery(query));
		System.out.println(resultSet.toString());
		
		new File("output").mkdirs();
		try (FileWriter writer = new FileWriter(new File("output/result.json"));
	              BufferedWriter bw = new BufferedWriter(writer)) {

	            bw.write(resultSet.toString());
	            System.out.println("Result.json file generated in output folder!");
	     } catch (Exception e) {
	            e.printStackTrace();
	     }
	}
}
