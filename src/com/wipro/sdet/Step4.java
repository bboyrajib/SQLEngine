package com.wipro.sdet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Step4 {
	public static void main(String[] args) {

		try {
			Scanner filePath = new Scanner(System.in);
			System.out.println("Enter absolute filepath to csv file: ");
			String filepath = filePath.nextLine();
			filePath.close();

			Scanner sc = new Scanner(new FileInputStream(new File(filepath)));

			// reading the headers
			String headersList = sc.nextLine();

			// reading the first row
			String firstRow = sc.nextLine();

			// Splitting & Storing headers and firstRow in an array
			String[] headers = headersList.split(",");
			String[] firstRowElems = firstRow.split(",");

			// Iterating through each element of firstRow to check the dataType
			for (int index = 0; index < headers.length; index++) {
				if (isValidInteger(firstRowElems[index].trim())) {
					System.out.println(headers[index].trim() + ":"
							+ new Integer(firstRowElems[index].trim()).getClass().getName());
				} else if (isValidDate(firstRowElems[index].trim())) {
					System.out.println(headers[index].trim() + ":" + new Date().getClass().getName());
				} else {
					System.out.println(headers[index].trim() + ":" + firstRowElems[index].trim().getClass().getName());
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static boolean isValidDate(String inDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	public static boolean isValidInteger(String s) {
		try {
			Integer temp = Integer.parseInt(s);
			temp=(int) temp;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
