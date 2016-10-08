package pl.mobilization.mobilizationcheckin;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by defecins on 04/10/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyHolder> {

    private static final String TAG = "MyAdapter";
    private final MainActivity mainActivity;
    private static final Collator POLISH_COLLATOR = Collator.getInstance(new Locale("pl", "PL"));
    private static final Comparator<User> comparatorOfUserLexicographically = new Comparator<User>() {
        @Override
        public int compare(User u1, User u2) {
            int compareLast = POLISH_COLLATOR.compare(u1.getLast(), u2.getLast());
            if(compareLast != 0)
                return compareLast;
            int compareFirst = POLISH_COLLATOR.compare(u1.getFirst(), u2.getFirst());
            if (compareFirst != 0)
                return compareFirst;
            return u1.getNumber().compareTo(u2.getNumber());
        }
    };

    Set<User> users = new TreeSet<>(comparatorOfUserLexicographically);
    Set<User> filteredUsers = new TreeSet<>(comparatorOfUserLexicographically);

    Predicate<User> predicate = Predicates.alwaysTrue();

    public MyAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView view = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.attendee, parent, false);
        return new MyHolder(view, this);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        User user = Iterators.get(filteredUsers.iterator(), position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return filteredUsers.size();
    }

    public void add(User user) {
        boolean setIsModified = users.add(user);

        if(!setIsModified)
            filteredUsers.remove(user);

        if(predicate.apply(user))
            filteredUsers.add(user);

        notifyDataSetChanged();
    }

    public void remove(User user) {
        users.remove(user);
        filteredUsers.remove(user);
        notifyDataSetChanged();
    }

    public void updateCheckedIn(User user, boolean checked) {
        user.setChecked(checked);
        mainActivity.updateCheckedIn(user);
    }

    public void setFilter(String filter) {
        final String normalizedFilter = normalize(filter);
        String[] normalizedFilters = normalizedFilter.split("\\s+");

        if(normalizedFilter.length() == 0) {
            predicate = Predicates.alwaysTrue();
            filteredUsers = new TreeSet<>(comparatorOfUserLexicographically);
            filteredUsers.addAll(filteredUsers);
            notifyDataSetChanged();
            return;
        }

        predicate = Predicates.and(FluentIterable.from(normalizedFilters).transform(new Function<String, Predicate<User>>() {
            @Override
            public Predicate<User> apply(final String subFilter) {
                return new Predicate<User>() {
                    @Override
                    public boolean apply(User user) {
                        return user.getFirstLCN().contains(subFilter) || user.getLastLCN().contains(subFilter) || user.getEmail().contains(subFilter) || user.getNumber().contains(subFilter);
                    }
                };
            }
        }));

        filteredUsers.clear();
        Iterables.addAll(filteredUsers, FluentIterable.from(users).filter(predicate));

        notifyDataSetChanged();
    }

    static Comparator<String> lexicographicalComparator = new Comparator<String>() {
        @Override
        public int compare(String str, String t1) {
            return str.compareTo(t1);
        }
    };

    static Function<User, String> user2NumberTransformation = new Function<User, String>() {
        @Override
        public String apply(User user) {
            return user.getNumber();
        }
    };

    private static String normalize(String str) {
        return  Normalizer.normalize(str.toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+","").replace("ł","l");

    }


}
