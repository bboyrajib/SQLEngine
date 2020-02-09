package com.wipro.sdet;

import java.util.ArrayList;
import java.util.List;

public class QueryParser {

	public QueryParameter parseQuery(String queryString) {

		queryString = queryString.replaceAll(";", "");
		String[] tokens = queryString.split(" ");
		String conditions = "", orderBy = "", groupBy = "";
		String[] conditionTokens;

		QueryParameter queryParameter = new QueryParameter();
		List<String> fields = new ArrayList<String>();
		List<String> logicalOperators = new ArrayList<String>();
		List<String> orderByFields = new ArrayList<String>();
		List<String> groupByFields = new ArrayList<String>();
		List<Filters> filtersList = new ArrayList<Filters>();
		List<AggregateFunctions> aggregateFunctionsList = new ArrayList<AggregateFunctions>();

		// setting query string
		queryParameter.setQueryString(queryString);

		// setting file
		for (int token = 0; token < tokens.length; token++)
			if (tokens[token].equalsIgnoreCase("from"))
				queryParameter.setFile(tokens[token + 1]);

		// setting baseQuery
		queryParameter.setBaseQuery(queryString.toLowerCase().split("where")[0].trim());

		// setting query_type
		queryParameter.setQUERY_TYPE(tokens[0]);

		// setting list of fields
		try {
			String fieldsTemp = queryString.toLowerCase().split("from")[0].split("select")[1].trim();
			String[] fieldsList = fieldsTemp.split(",");
			for (String field : fieldsList) {
				if (field.contains("min") || field.contains("max") || field.contains("count") || field.contains("avg")
						|| field.contains("sum"))
					continue;
				fields.add(field.trim());
			}
		} catch (Exception e) {
			// System.out.println("Error in parsing query!");
		}
		queryParameter.setFields(fields);

		// setting list of logical operators
		try {
			conditions = queryString.toLowerCase().split("where")[1].trim();
			conditionTokens = conditions.split(" ");
			for (String token : conditionTokens) {
				if (token.equals("and") || token.equals("or") || token.equals("not")) {
					logicalOperators.add(token);
				} else
					continue;
			}
		} catch (Exception e) {
			// System.out.println("Error in parsing query!");
		}
		queryParameter.setLogicalOperators(logicalOperators);

		// setting list of orderBy fields
		try {
			orderBy = queryString.toLowerCase().split("order by")[1].trim();
			String[] orderBys = orderBy.split(",");
			for (String token : orderBys)
				orderByFields.add(token.trim());
		} catch (Exception e) {
			// System.out.println("Error in parsing query!");
		}
		queryParameter.setOrderByField(orderByFields);

		// setting list of groupBy fields
		try {
			groupBy = queryString.toLowerCase().split("group by")[1].trim();
			groupBy = groupBy.split("order by")[0].trim();
			String[] groupBys = groupBy.split(",");
			for (String token : groupBys)
				groupByFields.add(token.trim());
		} catch (Exception e) {
			// System.out.println("Error in parsing query!");
		}
		queryParameter.setGroupByField(groupByFields);

		// setting filters
		Filters filters;
		try {
			conditions = queryString.toLowerCase().split("where")[1].trim();
			if (conditions.contains("group")) {
				conditions = conditions.split("group")[0].trim();
			} else if (conditions.contains("order")) {
				conditions = conditions.split("order")[0].trim();
			}
			String[] filtersInQuery = conditions.split("\\sand\\s|\\sor\\s|\\snot\\s");
			for (String filter : filtersInQuery) {
				String[] filterParts = filter.split("[<>=]+");
				filters = new Filters();
				filters.setPropertyName(filterParts[0].trim());
				filters.setPropertyValue(filterParts[1].trim().replaceAll("^\"|\"$|^'|'$", ""));
				if ("><=".indexOf(filter.split(filterParts[0].trim())[1].trim().charAt(0)) != -1
						&& "><=".indexOf(filter.split(filterParts[0].trim())[1].trim().charAt(1)) != -1) {
					filters.setCondition(filter.split(filterParts[0].trim())[1].trim().charAt(0) + ""
							+ filter.split(filterParts[0].trim())[1].trim().charAt(1));
				} else
					filters.setCondition(filter.split(filterParts[0].trim())[1].trim().charAt(0) + "");
				filtersList.add(filters);
			}
		} catch (Exception e) {
			// System.out.println("Error in parsing query!");
		}
		queryParameter.setRestrictions(filtersList);

		// setting aggregate functions
		try {
			AggregateFunctions aggregateFunctions;
			int index = 0;
			String aggregates = queryString.toLowerCase().split("from")[0].split("select")[1].trim();
			String[] aggregatesList = aggregates.split(",");
			for (String field : aggregatesList) {
				if (field.contains("min") || field.contains("max") || field.contains("count") || field.contains("avg")
						|| field.contains("sum")) {
					index++;
					aggregateFunctions = new AggregateFunctions();
					aggregateFunctions.setAggregateName(field.split("\\(")[0].trim());
					aggregateFunctions.setAggregateField(field.split("\\(")[1].split("\\)")[0].trim());
					aggregateFunctions.setIndex(index);
					aggregateFunctionsList.add(aggregateFunctions);
				} else
					continue;
			}
		} catch (Exception e) {
			// System.out.println("Error in parsing query!");
		}
		queryParameter.setAggregateFunctions(aggregateFunctionsList);

		return queryParameter;
	}
}
