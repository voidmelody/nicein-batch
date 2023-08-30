package kr.co.nice.nicein.vo;

public enum UpdateType {
    NEW("입사"),
    RESIGN("퇴사"),
    MODIFY("수정");

    private String value;

    UpdateType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
