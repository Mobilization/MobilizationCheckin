package pl.mobilization.mobilizationcheckin;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by defecins on 04/10/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyHolder> {

    private final MainActivity mainActivity;
    TreeMap<String, User> users = new TreeMap<>();

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
        String key = FluentIterable.from(users.keySet()).get(position);
        User user = users.get(key);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void add(User value) {
        users.put(value.number, value);
        notifyDataSetChanged();
    }

    public void remove(User user) {
        users.remove(user.number);
        notifyDataSetChanged();
    }

    public void updateCheckedIn(User user, boolean checked) {
        user.checked = checked;
        mainActivity.updateCheckedIn(user);
    }
}
