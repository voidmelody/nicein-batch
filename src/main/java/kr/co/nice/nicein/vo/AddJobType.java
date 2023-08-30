package kr.co.nice.nicein.vo;

public enum AddJobType {
    BASIC("기본"),
    ADDJOB("추가");

    private String value;

    AddJobType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
