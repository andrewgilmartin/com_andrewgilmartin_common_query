package com.andrewgilmartin.records;

public class RecordHit {

    private final Record record;
    private final float score;

    public RecordHit(Record record, float score) {
        this.record = record;
        this.score = score;
    }

    public float getScore() {
        return score;
    }

    public Record getRecord() {
        return record;
    }

}
