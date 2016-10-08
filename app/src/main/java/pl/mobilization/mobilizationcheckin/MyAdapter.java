package pl.mobilization.mobilizationcheckin;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by defecins on 04/10/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyHolder> {

    private static final String TAG = "MyAdapter";
    private final MainActivity mainActivity;
    Map<String, User> users = new TreeMap<>();
    List<String> filteredUsers = new ArrayList<>();
    Predicate<User> predicate = Predicates.alwaysTrue();

    public MyAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.attendee, parent, false);
        return new MyHolder(view, this);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        String key = filteredUsers.get(position);
        User user = users.get(key);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return filteredUsers.size();
    }

    public void add(User user) {
        User thisIsUpdate = users.put(user.getNumber(), user);
        if(thisIsUpdate == null) {
            if(predicate.apply(user))
                filteredUsers.add(user.getNumber());
        }
        notifyDataSetChanged();
    }

    public void remove(User user) {
        users.remove(user.getNumber());
        filteredUsers.remove(user.getNumber());
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
            filteredUsers = new ArrayList<String>(users.keySet());
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

        filteredUsers =  new ArrayList<>(FluentIterable.from(users.values()).filter(predicate).transform(user2NumberTransformation).toSortedList(lexicographicalComparator));

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
