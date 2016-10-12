package pl.mobilization.mobilizationcheckin;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by defecins on 04/10/16.
 */
public class AttendeeHolder extends RecyclerView.ViewHolder {
    private static final String TAG = AttendeeHolder.class.getSimpleName();
    private final AttendeesAdapter attendeesAdapter;
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



    public AttendeeHolder(CardView view, AttendeesAdapter attendeesAdapter) {
        super(view);
        this.attendeesAdapter = attendeesAdapter;

        ButterKnife.bind(this, itemView);
    }

    public void bind(final User user) {
        Log.d(TAG, String.format("binding user %s", user));
        textViewFirstName.setText(user.getFirst());
        textViewLastName.setText(user.getLast());

        textViewEmail.setText(user.getEmail());
        textViewType.setText(user.getType());
        textViewType.setBackgroundColor(typeToColor(user.getType()));

        checkBoxChecked.setOnCheckedChangeListener(null);
        checkBoxChecked.setChecked(user.isChecked());

        checkBoxChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                attendeesAdapter.updateCheckedIn(user, checked);
            }
        });
    }

    static Map<String, Integer> COLOR_MAP = new HashMap<>();

    static {
        COLOR_MAP.put("blind bird", 0xff00ff00);
        COLOR_MAP.put("early bird", 0xff00ee00);
        COLOR_MAP.put("regular", 0xff00dd00);
        COLOR_MAP.put("late bird", 0xff00cc00);
        COLOR_MAP.put("organizer", 0xffffff00);

        COLOR_MAP.put("vip", 0xffff0000);
        COLOR_MAP.put("speaker", 0xff0000ff);

    }

    private int typeToColor(String type) {
        Integer color = COLOR_MAP.get(type.toLowerCase());
        if(color != null)
            return color;
        return 0x00ffffff;

    }
}
