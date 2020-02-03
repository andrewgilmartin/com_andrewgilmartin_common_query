package com.andrewgilmartin.common.query;

public class NumberQuery extends Query {

    private final String field;
    private final Number number;

    public NumberQuery(float weight, String field, Number number) {
        super(weight);
        this.field = field;
        this.number = number;
    }

    public NumberQuery(String field, Number number) {
        this(Float.NaN, field, number);
    }

    public String getField() {
        return field;
    }

    public Number getNumber() {
        return number;
    }
}

// END
