package pl.mobilization.mobilizationcheckin;

/**
 * Created by defecins on 03/10/16.
 */
public class User {
    public String first;
    public String last;
    public String email;
    public String type;
    public String number;
    public boolean checked;


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
}
