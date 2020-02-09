package com.wipro.sdet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Step3 {

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
				try {
					Integer temp = Integer.parseInt(firstRowElems[index].trim());
					System.out.println(headers[index].trim() + ":" + temp.getClass().getName());
				} catch (Exception e) {
					System.out.println(headers[index].trim() + ":" + firstRowElems[index].trim().getClass().getName());
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
