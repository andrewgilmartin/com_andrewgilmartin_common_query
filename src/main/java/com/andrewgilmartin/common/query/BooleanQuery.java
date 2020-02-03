package com.andrewgilmartin.common.query;

public class BooleanQuery extends Query {

    private final String field;
    private final boolean value;

    public BooleanQuery(float weight, String field, boolean value) {
        super(weight);
        this.field = field;
        this.value = value;
    }

    public BooleanQuery(String field, boolean value) {
        this(Float.NaN, field, value);
    }

    public String getField() {
        return field;
    }

    public boolean getBoolean() {
        return value;
    }
}

// END
