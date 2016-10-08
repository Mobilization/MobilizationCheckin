package pl.mobilization.mobilizationcheckin;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by defecins on 04/10/16.
 */
public class MyHolder extends RecyclerView.ViewHolder {
    private static final String TAG = MyHolder.class.getSimpleName();
    private final MyAdapter myAdapter;
    @BindView(R.id.textViewFirstName)
    TextView textViewFirstName;

    @BindView(R.id.textViewLastName)
    TextView textViewLastName;

    @BindView(R.id.textViewEmail)
    TextView textViewEmail;

    @BindView(R.id.textViewType)
    TextView textViewType;

    @BindView(R.id.checkBoxChecked)
    CheckBox checkBoxChecked;

    public MyHolder(RelativeLayout view, MyAdapter myAdapter) {
        super(view);
        this.myAdapter = myAdapter;

        ButterKnife.bind(this, itemView);
    }

    public void bind(final User user) {
        Log.d(TAG, String.format("binding user %s", user));
        textViewFirstName.setText(user.getFirst());
        textViewLastName.setText(user.getLast());

        textViewEmail.setText(user.getEmail());
        textViewType.setText(user.getType());

        checkBoxChecked.setOnCheckedChangeListener(null);
        checkBoxChecked.setChecked(user.isChecked());

        checkBoxChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                myAdapter.updateCheckedIn(user, checked);
            }
        });
    }
}
