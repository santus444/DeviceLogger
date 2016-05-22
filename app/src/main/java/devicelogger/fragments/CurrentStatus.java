package devicelogger.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.santoshmandadi.deviceloggerone.R;
import com.santoshmandadi.deviceloggerone.model.Device;
import com.santoshmandadi.deviceloggerone.utils.Constants;

/**
 * Created by santosh on 5/19/16.
 */
public class CurrentStatus extends BaseFragment {

    FirebaseListAdapter<Device> deviceFirebaseListAdapter;
    Firebase firebaseRef;
    ListView deviceStatusListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_status, container, false);
        initialize(rootView);
        if (firebaseRef.getAuth() != null) {
            deviceFirebaseListAdapter = new FirebaseListAdapter<Device>(getActivity(), Device.class, R.layout.list_item_device_status, firebaseRef.child(firebaseRef.getAuth().getUid()).child(Constants.FIREBASE_LOCATION_CURRENT_LIST)) {
                @Override
                protected void populateView(View v, Device model) {
                    // super.populateView(v, model);
                    ((TextView) v.findViewById(R.id.deviceName)).setText(model.getDeviceName());
                    ((TextView) v.findViewById(R.id.userName)).setText(model.getUser());
                    if (model.getUser().equalsIgnoreCase(ScanFragment.AVAILABLE_SERVER_TAG)) {
                        v.setContentDescription(model.getDeviceName() + " is " + model.getUser());
                        ((TextView) v.findViewById(R.id.userName)).setTextColor(Color.GREEN);
                        ((TextView) v.findViewById(R.id.userName)).setTypeface(Typeface.DEFAULT_BOLD);
                    } else {
                        v.setContentDescription(model.getDeviceName() + " is with " + model.getUser());
                        ((TextView) v.findViewById(R.id.userName)).setTextColor(Color.RED);
                        ((TextView) v.findViewById(R.id.userName)).setTypeface(Typeface.DEFAULT_BOLD);
                    }

                }
            };
            deviceStatusListView.setAdapter(deviceFirebaseListAdapter);
            View emptyView = rootView.findViewById(R.id.listview_devices_empty);
            deviceStatusListView.setEmptyView(emptyView);

        } else {
            showErrorSnackBar(rootView, getString(R.string.error_message_current_status_no_login));
        }
        return rootView;
    }

    private void initialize(View view) {
        firebaseRef = new Firebase(Constants.FIREBASE_URL);
        deviceStatusListView = (ListView) view.findViewById(R.id.listview_status);
    }


}
