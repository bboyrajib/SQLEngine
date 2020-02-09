package com.wipro.sdet;

import java.util.Scanner;

public class Step1 {

	public static void main(String[] args) {

		String conditions = "";
		Scanner sc = new Scanner(System.in);
		String query = sc.nextLine();
		sc.close();

		// Step 1: a
		System.out.println("Step 1:a");
		String[] tokens = query.split(" ");
		for (String token : tokens)
			System.out.println(token);

		System.out.println();

		// Step 1:b (i)
		System.out.println("Step 1:b (i)");
		for (int token = 0; token < tokens.length; token++)
			if (tokens[token].equalsIgnoreCase("from"))
				System.out.println(tokens[token + 1]);

		System.out.println();

		// Step 1:b (ii)
		System.out.println("Step 1:b (ii)");
		System.out.println(query.toLowerCase().split("where")[0].trim());

		System.out.println();

		// Step 1:b (iii)
		System.out.println("Step 1:b (iii)");
		try {
			String[] afterWhere = query.toLowerCase().split("where")[1].trim().split(" ");
			for (String token : afterWhere) {
				if (token.equalsIgnoreCase("group") || token.equalsIgnoreCase("order")) {
					break;
				} else
					System.out.print(token + " ");
			}
		} catch (Exception e) {

		}
		System.out.println();
		System.out.println();

		// Step 1:b (iv)
		System.out.println("Step 1:b (iv)");
		try {
			// List<String> filtersList = new ArrayList<String>();
			// String temp = "";
			conditions = query.toLowerCase().split("where")[1].trim();
			if (conditions.contains("group")) {
				conditions = conditions.split("group")[0].trim();
			} else if (conditions.contains("order")) {
				conditions = conditions.split("order")[0].trim();
			}
			String[] filters = conditions.split("\\sand\\s|\\sor\\s|\\snot\\s");
			for (String filter : filters) {
				System.out.println(filter.trim());
//				String[] filterParts = filter.split("[<>=]+");
//				System.out.println(filterParts[0].trim());
//				System.out.println(filterParts[1].trim());
//				if ("><=".indexOf(filter.split(filterParts[0].trim())[1].trim().charAt(0)) != -1
//						&& "><=".indexOf(filter.split(filterParts[0].trim())[1].trim().charAt(1)) != -1) {
//					System.out.println(filter.split(filterParts[0].trim())[1].trim().charAt(0)
//							+""+ filter.split(filterParts[0].trim())[1].trim().charAt(1));
//
//				} else
//					System.out.println(filter.split(filterParts[0].trim())[1].trim().charAt(0));
			}
		} catch (Exception e) {

		}
		System.out.println();

		// Step 1:b (v)
		System.out.println("Step 1:b (v)");
		try {
			String conditionTokens[] = conditions.split(" ");
			for (String token : conditionTokens) {
				if (token.equals("and") || token.equals("or") || token.equals("not")) {
					System.out.println(token);
				} else
					continue;
			}
		} catch (Exception e) {

		}
		System.out.println();

		// Step 1:b (vi)
		System.out.println("Step 1:b (vi)");
		String fields = query.toLowerCase().split("from")[0].split("select")[1].trim();
		String[] fieldsList = fields.split(",");
		for (String field : fieldsList) {
			if (field.contains("min") || field.contains("max") || field.contains("count") || field.contains("avg")
					|| field.contains("sum"))
				continue;
			System.out.println(field.trim());
		}
		System.out.println();

		// Step 1:b (vii)
		System.out.println("Step 1:b (vii)");
		try {
			System.out.println(query.toLowerCase().split("order by")[1].trim());
		} catch (Exception e) {

		}
		System.out.println();

		// Step 1:b (viii)
		System.out.println("Step 1:b (viii)");
		try {
			System.out.println(query.toLowerCase().split("group by")[1].trim().split("order by")[0].trim());
		} catch (Exception e) {

		}
		System.out.println();

		// Step 1:b (ix)
		System.out.println("Step 1:b (ix)");
		String aggregates = query.toLowerCase().split("from")[0].split("select")[1].trim();
		String[] aggregatesList = aggregates.split(",");
		for (String field : aggregatesList) {
			if (field.contains("min") || field.contains("max") || field.contains("count") || field.contains("avg")
					|| field.contains("sum")) {
				System.out.println(field.trim());
				// System.out.println(field.split("\\(")[0].trim());
				// System.out.println(field.split("\\(")[1].split("\\)")[0].trim());
			} else
				continue;
		}
		System.out.println();

	}
}
