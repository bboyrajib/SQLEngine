package com.wipro.sdet;

public class AggregateFunctions {

	String aggregateName, aggregateField;
	int result, index;

	public AggregateFunctions() {
		aggregateName = "";
		aggregateField = "";
		result = 0;
		index = 0;
	}

	public String getAggregateName() {
		return aggregateName;
	}

	public void setAggregateName(String aggregateName) {
		this.aggregateName = aggregateName;
	}

	public String getAggregateField() {
		return aggregateField;
	}

	public void setAggregateField(String aggregateField) {
		this.aggregateField = aggregateField;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
