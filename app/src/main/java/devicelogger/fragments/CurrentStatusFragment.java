package devicelogger.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;
import com.santoshmandadi.deviceloggerone.AsyncTasks.FetchDevicesStatusTask;
import com.santoshmandadi.deviceloggerone.R;
import com.santoshmandadi.deviceloggerone.adapters.DevicesAdapter;
import com.santoshmandadi.deviceloggerone.data.DeviceLoggerContract;
import com.santoshmandadi.deviceloggerone.model.Device;
import com.santoshmandadi.deviceloggerone.model.DeviceObject;
import com.santoshmandadi.deviceloggerone.utils.Constants;

import java.util.ArrayList;

/**
 * Created by santosh on 5/19/16.
 */
public class CurrentStatusFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String[] DEVICES_COLUMNS = {
            DeviceLoggerContract.DevicesEntry.TABLE_NAME + "." + DeviceLoggerContract.DevicesEntry._ID,
            DeviceLoggerContract.DevicesEntry.COLUMN_SERIAL_NUMBER,
            DeviceLoggerContract.DevicesEntry.COLUMN_DEVICE_NAME,
            DeviceLoggerContract.DevicesEntry.COLUMN_USER_NAME};
    public static final int COL_DEVICES_TABLE_ID = 0;
    public static final int COL_DEVICE_SERIAL_NUMBER = 1;
    public static final int COL_DEVICE_NAME = 2;
    public static final int COL_USER_NAME = 3;
    private static final int DEVICE_LOADER = 0;
    final String LOG_TAG = CurrentStatusFragment.class.getSimpleName();
    FirebaseListAdapter<Device> deviceFirebaseListAdapter;
    Firebase firebaseRef;
    ListView deviceStatusListView;
    ArrayList<DeviceObject> listOfDeviceObjects = new ArrayList<>();

    private DevicesAdapter devicesStatusAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_status, container, false);
        initialize(rootView);
        View emptyView = rootView.findViewById(R.id.listview_devices_empty);
        deviceStatusListView.setEmptyView(emptyView);
        if (firebaseRef.getAuth() != null) {
            Firebase listNameRef = new Firebase(Constants.FIREBASE_URL).child(firebaseRef.getAuth().getUid()).child(Constants.FIREBASE_LOCATION_CURRENT_LIST);
            listNameRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    FetchDevicesStatusTask fetchArtistsTask = new FetchDevicesStatusTask(getActivity().getApplicationContext());
                    fetchArtistsTask.execute(dataSnapshot);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    switch (firebaseError.getCode()) {
                        case FirebaseError.DISCONNECTED: {
                            //noNetwork = true;
                        }

                    }
                }
            });
//            deviceFirebaseListAdapter = new FirebaseListAdapter<Device>(getActivity(), Device.class, R.layout.list_item_device_status, firebaseRef.child(firebaseRef.getAuth().getUid()).child(Constants.FIREBASE_LOCATION_CURRENT_LIST)) {
//                @Override
//                protected void populateView(View v, Device model) {
//                    // super.populateView(v, model);
//                    ((TextView) v.findViewById(R.id.list_item_deviceName)).setText(model.getDeviceName());
//                    ((TextView) v.findViewById(R.id.list_item_userName)).setText(model.getUser());
//                    if (model.getUser().equalsIgnoreCase(ScanFragment.AVAILABLE_SERVER_TAG)) {
//                        v.setContentDescription(model.getDeviceName() + " is " + model.getUser());
//                        ((TextView) v.findViewById(R.id.list_item_userName)).setTextColor(Color.GREEN);
//                        ((TextView) v.findViewById(R.id.list_item_userName)).setTypeface(Typeface.DEFAULT_BOLD);
//                    } else {
//                        v.setContentDescription(model.getDeviceName() + " is with " + model.getUser());
//                        ((TextView) v.findViewById(R.id.list_item_userName)).setTextColor(Color.RED);
//                        ((TextView) v.findViewById(R.id.list_item_userName)).setTypeface(Typeface.DEFAULT_BOLD);
//                    }
//
//                }
//            };
//            deviceStatusListView.setAdapter(deviceFirebaseListAdapter);
            devicesStatusAdapter = new DevicesAdapter(getActivity(), null, 0);
            deviceStatusListView.setAdapter(devicesStatusAdapter);


        } else {
            showErrorSnackBar(rootView, getString(R.string.error_message_current_status_no_login));
        }
        return rootView;
    }

    private void initialize(View view) {
        firebaseRef = new Firebase(Constants.FIREBASE_URL);
        deviceStatusListView = (ListView) view.findViewById(R.id.listview_status);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (firebaseRef.getAuth() != null) {

            getLoaderManager().initLoader(DEVICE_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = DeviceLoggerContract.DevicesEntry.COLUMN_DEVICE_NAME + " ASC";
        return new CursorLoader(getActivity(), DeviceLoggerContract.DevicesEntry.CONTENT_URI, DEVICES_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        devicesStatusAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        devicesStatusAdapter.swapCursor(null);
    }

}
