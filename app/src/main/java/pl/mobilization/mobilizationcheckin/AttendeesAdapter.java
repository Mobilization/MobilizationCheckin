package pl.mobilization.mobilizationcheckin;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import java.text.Collator;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by defecins on 04/10/16.
 */
public class AttendeesAdapter extends RecyclerView.Adapter<AttendeeHolder> {

    private static final String TAG = "AttendeesAdapter";
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
    private FirebaseService service;

    private BehaviorSubject<Long> totalCountSubject = BehaviorSubject.create(Long.valueOf(0));
    private BehaviorSubject<Long> checkedCountSubject = BehaviorSubject.create(Long.valueOf(0));
    private BehaviorSubject<Float> stalaSaramakSubject = BehaviorSubject.create(Float.valueOf(0));
    private int checkedInCount;


    public AttendeesAdapter(FirebaseService service) {
        this.service = service;
        setHasStableIds(true);
    }

    @Override
    public AttendeeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView view = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.attendee, parent, false);
        return new AttendeeHolder(view, this);
    }

    @Override
    public void onBindViewHolder(AttendeeHolder holder, int position) {
        User user = getUserAtPosition(position);
        holder.bind(user);
    }

    private User getUserAtPosition(int position) {
        return Iterators.get(filteredUsers.iterator(), position);
    }

    @Override
    public int getItemCount() {
        return filteredUsers.size();
    }

    public void addOrUpdate(final User newUser) {
        final User found = findUser(newUser);

        if(found != null) {
            users.remove(found);
            filteredUsers.remove(found);
        }
        users.add(newUser);

        if(predicate.apply(newUser))
            filteredUsers.add(newUser);

        totalCountSubject.onNext(Long.valueOf(users.size()));
        checkedCountSubject.onNext(Long.valueOf(getCheckedInCount()));
        stalaSaramakSubject.onNext(Float.valueOf(getStalaSaramaka()));

        notifyDataSetChanged();
    }

    private User findUser(final User user) {
        return Iterables.getFirst(FluentIterable.from(users).filter(new Predicate<User>() {
            @Override
            public boolean apply(User input) {
                return input.getNumber().equals(user.getNumber());
            }
        }), null);
    }

    private float getStalaSaramaka() {
        if(users.size() == 0)
            return 0.0f;

        return (100.0f*checkedInCount)/users.size();
    }

    private boolean wasUserUpdate(boolean setIsModified) {
        return !setIsModified;
    }

    public void remove(User user) {
        users.remove(user);
        filteredUsers.remove(user);
        notifyDataSetChanged();
    }

    public void setFilter(String filter) {
        final String normalizedFilter = normalize(filter);
        String[] normalizedFilters = normalizedFilter.split("\\s+");

        if(normalizedFilter.length() == 0) {
            predicate = Predicates.alwaysTrue();
            filteredUsers.clear();
            filteredUsers.addAll(users);
            notifyDataSetChanged();
            return;
        }

        predicate = Predicates.and(FluentIterable.from(normalizedFilters).transform(new Function<String, Predicate<User>>() {
            @Override
            public Predicate<User> apply(final String subFilter) {
                return new Predicate<User>() {
                    @Override
                    public boolean apply(User user) {
                        return user.getFirstLCN().contains(subFilter)
                                || user.getLastLCN().contains(subFilter)
                                || user.getEmail().contains(subFilter)
                                || user.getNumber().contains(subFilter)
                                || user.getOrderid().contains(subFilter);
                    }
                };
            }
        }));

        filteredUsers.clear();
        Iterables.addAll(filteredUsers, FluentIterable.from(users).filter(predicate));
        checkedCountSubject.onNext(Long.valueOf(getCheckedInCount()));

        notifyDataSetChanged();
    }

    private int getCheckedInCount() {
        return checkedInCount = FluentIterable.from(users).filter(CHECKED_IN_PREDICATE).size();
    }

    static Comparator<String> lexicographicalComparator = new Comparator<String>() {
        @Override
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    };

    static Function<User, String> USER_TO_NUMBER_TRANSFORMATION = new Function<User, String>() {
        @Override
        public String apply(User user) {
            return user.getNumber();
        }
    };

    private static String normalize(String str) {
        return  Normalizer.normalize(str.toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+","").replace("ł","l");
    }

    public void updateCheckedIn(User user, boolean checked) {
        user.setChecked(checked);
        service.updateCheckedIn(user);
    }

    static Predicate<User> CHECKED_IN_PREDICATE = new Predicate<User>() {

        @Override
        public boolean apply(User input) {
            return input.isChecked();
        }
    };

    @Override
    public long getItemId(int position) {
        return Long.parseLong(getUserAtPosition(position).getNumber());
    }

    public Observable<Long> totalCount$() {
        return totalCountSubject.asObservable();
    }

    public Observable<Long> checkedInCount$() {
        return checkedCountSubject.asObservable();
    }

    public Observable<Float> stalaSaramaka$() {
        return stalaSaramakSubject.asObservable();
    }
}
