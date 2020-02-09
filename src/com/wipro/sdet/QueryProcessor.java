package com.wipro.sdet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class QueryProcessor {

	JSONObject resultSet = new JSONObject("{}");
	int resultSetNo = 0;
	List<HashMap<String, String>> csvFileData = new ArrayList<HashMap<String, String>>();
	List<String> fieldsList = new ArrayList<>();
	boolean selectAllColumns = false;
	List<Filters> filtersList = new ArrayList<Filters>();
	List<String> filterNames = new ArrayList<>();
	List<String> filterValues = new ArrayList<>();
	List<String> filterConds = new ArrayList<>();
	List<String> logicalOps = new ArrayList<>();
	static Map<String, Operator> opMap = new HashMap<String, Operator>();
	List<HashMap<String, String>> updatedList = new ArrayList<HashMap<String, String>>();
	Predicate<HashMap<String, String>> predicate, finalPredicate;
	List<Predicate<HashMap<String, String>>> predicateList = new ArrayList<Predicate<HashMap<String, String>>>();
	List<String> orderByFields = new ArrayList<>();
	List<Comparator<Map<String, String>>> comparators = new ArrayList<Comparator<Map<String, String>>>();
	List<String> groupByFields = new ArrayList<>();
	List<AggregateFunctions> aggregateFunctions = new ArrayList<AggregateFunctions>();

	public JSONObject processQuery(QueryParameter queryParameter) {

		try {
			// reading the csv file
			Scanner sc = new Scanner(new FileInputStream(new File(queryParameter.getFile())));

			// fetching the headers
			String[] headers = sc.nextLine().split(",");

			// storing the csv file data in a list of hashmaps
			HashMap<String, String> rowMap;
			String[] rowData;
			while (sc.hasNextLine()) {

				rowMap = new HashMap<>();
				rowData = sc.nextLine().split(",");
				for (int index = 0; index < headers.length; index++) {
					rowMap.put(headers[index].trim(), rowData[index].trim());
				}
				csvFileData.add(rowMap);
			}

			// getting list of fields selected by user
			fieldsList = queryParameter.getFields();

			// checking if it contains *
			for (String field : fieldsList) {
				if (field.equals("*")) {
					selectAllColumns = true;
					break;
				}
			}

			// getting list of filters and their properties
			filtersList = queryParameter.getRestrictions();
			for (Filters filter : filtersList) {
				filterNames.add(filter.getPropertyName());
				filterValues.add(filter.getPropertyValue());
				filterConds.add(filter.getCondition());
			}

			// getting list of logical operators
			logicalOps = queryParameter.getLogicalOperators();

			// setting operators
			setOperators();

			// apply the filters
			if (filtersList.size() < 1) {
				updatedList = csvFileData;
			} else {

				for (int index = 0; index < filtersList.size(); index++) {
					String filterOperator = filterConds.get(index).trim();
					String filterName = filterNames.get(index).trim();
					String filterValue = filterValues.get(index).trim();
					predicate = row -> opMap.get(filterOperator).compare(row.get(filterName), filterValue);
					predicateList.add(predicate);
				}

				// use the logical operators to join the filters
				finalPredicate = predicateList.get(0);
				for (int logicalOp = 0; logicalOp < logicalOps.size(); logicalOp++) {
					switch (logicalOps.get(logicalOp)) {
					case "and":
						finalPredicate = finalPredicate.and(predicateList.get(logicalOp + 1));
						continue;
					case "or":
						finalPredicate = finalPredicate.or(predicateList.get(logicalOp + 1));
						continue;
					}
				}
				try {
					updatedList = csvFileData.stream().filter(finalPredicate).collect(Collectors.toList());
				} catch (Exception e) {

				}

			}

			// get all groupBy fields
			groupByFields = queryParameter.getGroupByField();

			// get all the orderBy fields and add it to a list of comparators
			orderByFields = queryParameter.getOrderByField();
			for (String orderByField : orderByFields) {
				String key = orderByField.trim();
				Comparator<Map<String, String>> newComparator = new Comparator<Map<String, String>>() {
					public int compare(Map<String, String> m1, Map<String, String> m2) {
						if (m1.get(key) != null && m2.get(key) != null)
							return m1.get(key).compareTo(m2.get(key));
						else
							return 0;
					}
				};
				comparators.add(newComparator);
			}
			// construct the final comaparator object
			Comparator<Map<String, String>> finalComparator = new Comparator<Map<String, String>>() {
				public int compare(Map<String, String> m1, Map<String, String> m2) {
					for (Comparator<Map<String, String>> comparator : comparators) {
						int result = comparator.compare(m1, m2);
						if (result != 0) {
							return result;
						}
					}
					return 0;
				}
			};

			// sort the list based on orderBy fields
			Collections.sort(updatedList, finalComparator);

			// get all aggregate functions
			aggregateFunctions = queryParameter.getAggregateFunctions();
			HashMap<String, String> aggregateResultSet = new HashMap<String, String>();
			if (groupByFields.size() < 1) {
				for (AggregateFunctions AF : aggregateFunctions) {
					int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, sum = 0;
					String name = AF.getAggregateName();
					String field = AF.getAggregateField();
					if (name.equalsIgnoreCase("count")) {
						aggregateResultSet.put(name + "(" + field + ")", updatedList.size() + "");
					} else if (name.equalsIgnoreCase("min")) {
						for (HashMap<String, String> map : updatedList) {
							if (!isValidInteger(map.get(field))) {
								continue;
							}
							if (Integer.parseInt(map.get(field)) < min)
								min = Integer.parseInt(map.get(field));

						}
						aggregateResultSet.put(name + "(" + field + ")", min + "");
					} else if (name.equalsIgnoreCase("max")) {
						for (HashMap<String, String> map : updatedList) {
							if (!isValidInteger(map.get(field))) {
								continue;
							}
							if (Integer.parseInt(map.get(field)) > max)
								max = Integer.parseInt(map.get(field));
						}
						aggregateResultSet.put(name + "(" + field + ")", max + "");
					} else if (name.equalsIgnoreCase("avg")) {
						int temp = 0, count = 0;
						double avg = 0.0;
						for (HashMap<String, String> map : updatedList) {
							if (!isValidInteger(map.get(field))) {
								continue;
							}
							temp += Integer.parseInt(map.get(field));
							count++;
						}
						avg = temp / count;
						aggregateResultSet.put(name + "(" + field + ")", avg + "");
					} else if (name.equalsIgnoreCase("sum")) {
						for (HashMap<String, String> map : updatedList) {
							if (!isValidInteger(map.get(field))) {
								continue;
							}
							sum += Integer.parseInt(map.get(field));
						}
						aggregateResultSet.put(name + "(" + field + ")", sum + "");
					}
				}
			} else {
				// use stream to group rows

			}

			if (aggregateFunctions.size() > 0 && fieldsList.size() >= 0) {
				this.resultSet = new JSONObject(aggregateResultSet);
			}

			if (aggregateFunctions.size() < 1 && fieldsList.size() > 0) {
				// select the fields and add the rows to the final result set
				List<HashMap<String, String>> resultSet = new ArrayList<>();
				HashMap<String, String> rowDataMap;
				if (selectAllColumns) {
					this.resultSet = convertListToJSON(updatedList);

				} else {

					for (HashMap<String, String> row : updatedList) {
						rowDataMap = new HashMap<>();
						for (String field : fieldsList) {
							field = field.trim();
							if (row.get(field) != null) {
								rowDataMap.put(field, row.get(field));
							}
						}
						resultSet.add(rowDataMap);
					}
					this.resultSet = convertListToJSON(resultSet);
				}
			}

			sc.close();

		} catch (FileNotFoundException e) {
			System.out.println(e);
			return resultSet;
		} catch (Exception e) {
			System.out.println("Error while processing query! " + e);
			return resultSet;
		}
		return resultSet;
	}

	public JSONObject convertListToJSON(List<HashMap<String, String>> resultSetList) {

		JSONObject finalObject = new JSONObject("{}");
		int index = 1;
		for (HashMap<String, String> field : resultSetList) {
			finalObject.put(index + "", new JSONObject(field));
			index++;
		}

		return finalObject;
	}

	interface Operator {

		boolean compare(String a, String b);
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
			temp = (int) temp;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static void setOperators() {
		// setting the meaning of each logical operator in a map

		opMap.put(">", new Operator() {
			boolean result = false;

			@Override
			public boolean compare(String a, String b) {

				if (isValidDate(a) && isValidDate(b)) {

					try {
						result = new SimpleDateFormat("yyyy-MM-dd").parse(a)
								.compareTo(new SimpleDateFormat("yyyy-MM-dd").parse(b)) > 0;
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (isValidInteger(a) && isValidInteger(b)) {
					result = Integer.parseInt(a) > Integer.parseInt(b);
				}

				return result;
			}
		});

		opMap.put("<", new Operator() {
			boolean result = false;

			@Override
			public boolean compare(String a, String b) {

				if (isValidDate(a) && isValidDate(b)) {

					try {
						result = new SimpleDateFormat("yyyy-MM-dd").parse(a)
								.compareTo(new SimpleDateFormat("yyyy-MM-dd").parse(b)) < 0;
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (isValidInteger(a) && isValidInteger(b)) {
					result = Integer.parseInt(a) < Integer.parseInt(b);
				}

				return result;
			}
		});

		opMap.put(">=", new Operator() {
			boolean result = false;

			@Override
			public boolean compare(String a, String b) {

				if (isValidDate(a) && isValidDate(b)) {

					try {
						result = new SimpleDateFormat("yyyy-MM-dd").parse(a)
								.compareTo(new SimpleDateFormat("yyyy-MM-dd").parse(b)) >= 0;
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (isValidInteger(a) && isValidInteger(b)) {
					result = Integer.parseInt(a) >= Integer.parseInt(b);
				}

				return result;
			}
		});

		opMap.put("<=", new Operator() {
			boolean result = false;

			@Override
			public boolean compare(String a, String b) {

				if (isValidDate(a) && isValidDate(b)) {

					try {
						result = new SimpleDateFormat("yyyy-MM-dd").parse(a)
								.compareTo(new SimpleDateFormat("yyyy-MM-dd").parse(b)) <= 0;
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (isValidInteger(a) && isValidInteger(b)) {
					result = Integer.parseInt(a) <= Integer.parseInt(b);
				}

				return result;
			}
		});

		opMap.put("=", new Operator() {
			@Override
			public boolean compare(String a, String b) {
				boolean result = false;
				if (isValidDate(a) && isValidDate(b)) {

					try {
						result = new SimpleDateFormat("yyyy-MM-dd").parse(a)
								.compareTo(new SimpleDateFormat("yyyy-MM-dd").parse(b)) == 0;
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (isValidInteger(a) && isValidInteger(b)) {
					result = Integer.parseInt(a) == Integer.parseInt(b);
				} else {

					result = a.equalsIgnoreCase(b);
				}

				return result;

			}
		});

		opMap.put("<>", new Operator() {
			@Override
			public boolean compare(String a, String b) {
				boolean result = false;
				if (isValidDate(a) && isValidDate(b)) {

					try {
						result = new SimpleDateFormat("yyyy-MM-dd").parse(a)
								.compareTo(new SimpleDateFormat("yyyy-MM-dd").parse(b)) != 0;
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (isValidInteger(a) && isValidInteger(b)) {
					result = Integer.parseInt(a) != Integer.parseInt(b);
				} else
					result = !(a.equalsIgnoreCase(b));

				return result;

			}
		});

	}
}
