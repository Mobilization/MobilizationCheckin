package pl.mobilization.mobilizationcheckin;

import com.google.common.base.Strings;
import com.google.firebase.database.Exclude;

import java.text.Normalizer;

/**
 * Created by defecins on 03/10/16.
 */
public class User {
    private String first;
    private String last;
    private String email;
    private String type;
    private String number;
    private boolean checked;
    private String orderid;
    @Exclude
    private String firstLCN;
    @Exclude
    private String lastLCN;

    public User() {
    }

    public static String normalize(String str) {
        return Normalizer.normalize(Strings.nullToEmpty(str).toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replace("ł", "l");
    }

    public String getOrderid() {
        return Strings.nullToEmpty(orderid);
    }

    public void setOrderid(String orderid) {
        this.orderid = Strings.nullToEmpty(orderid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return number != null ? number.equals(user.number) : user.number == null;

    }

    @Override
    public int hashCode() {
        return number != null ? number.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "first='" + first + '\'' +
                ", last='" + last + '\'' +
                ", email='" + email + '\'' +
                ", type='" + type + '\'' +
                ", number='" + number + '\'' +
                ", orderid='" + orderid + '\'' +
                ", checked=" + checked +
                '}';
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
        this.firstLCN = normalize(first);
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
        this.lastLCN = normalize(last);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Strings.nullToEmpty(email);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = Strings.nullToEmpty(type);
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = Strings.nullToEmpty(number);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Exclude
    public String getFirstLCN() {
        return firstLCN;
    }

    @Exclude
    public String getLastLCN() {
        return lastLCN;
    }
}
