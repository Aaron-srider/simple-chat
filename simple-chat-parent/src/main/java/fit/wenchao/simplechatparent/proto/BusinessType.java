package fit.wenchao.simplechatparent.proto;

import java.io.Serializable;
import java.util.Objects;

public class BusinessType implements IBusinessType, Serializable {
    private String type;

    public BusinessType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "BusinessType{" +
                "type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessType that = (BusinessType) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
