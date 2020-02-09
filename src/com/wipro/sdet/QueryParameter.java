package com.wipro.sdet;

import java.util.ArrayList;
import java.util.List;

public class QueryParameter {

	String queryString, file, baseQuery, QUERY_TYPE;
	List<String> fields, logicalOperators, orderByField, groupByField;
	List<Filters> restrictions;
	List<AggregateFunctions> aggregateFunctions;

	public QueryParameter() {
		queryString = "";
		file = "";
		baseQuery = "";
		QUERY_TYPE = "";
		fields = new ArrayList<String>();
		logicalOperators = new ArrayList<String>();
		orderByField = new ArrayList<String>();
		groupByField = new ArrayList<String>();
		restrictions = new ArrayList<Filters>();
		aggregateFunctions = new ArrayList<AggregateFunctions>();
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getBaseQuery() {
		return baseQuery;
	}

	public void setBaseQuery(String baseQuery) {
		this.baseQuery = baseQuery;
	}

	public String getQUERY_TYPE() {
		return QUERY_TYPE;
	}

	public void setQUERY_TYPE(String qUERY_TYPE) {
		QUERY_TYPE = qUERY_TYPE;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public List<String> getLogicalOperators() {
		return logicalOperators;
	}

	public void setLogicalOperators(List<String> logicalOperators) {
		this.logicalOperators = logicalOperators;
	}

	public List<String> getOrderByField() {
		return orderByField;
	}

	public void setOrderByField(List<String> orderByField) {
		this.orderByField = orderByField;
	}

	public List<String> getGroupByField() {
		return groupByField;
	}

	public void setGroupByField(List<String> groupByField) {
		this.groupByField = groupByField;
	}

	public List<Filters> getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(List<Filters> restrictions) {
		this.restrictions = restrictions;
	}

	public List<AggregateFunctions> getAggregateFunctions() {
		return aggregateFunctions;
	}

	public void setAggregateFunctions(List<AggregateFunctions> aggregateFunctions) {
		this.aggregateFunctions = aggregateFunctions;
	}
}
