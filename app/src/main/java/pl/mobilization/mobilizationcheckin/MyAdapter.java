package pl.mobilization.mobilizationcheckin;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by defecins on 04/10/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyHolder> {
    List<User> users = new ArrayList<>();

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.attendee, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        User user = users.get(position);

        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void add(User value) {
        users.add(value);
        notifyDataSetChanged();
    }

    public void update(User value) {
        users.remove(value);
        users.
    }
}
