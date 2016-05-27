package devicelogger.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.santoshmandadi.deviceloggerone.AsyncTasks.FetchDevicesStatusTask;
import com.santoshmandadi.deviceloggerone.R;
import com.santoshmandadi.deviceloggerone.model.Device;
import com.santoshmandadi.deviceloggerone.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by santosh on 5/17/16.
 */
public class ScanFragment extends BaseFragment {
    public static final String AVAILABLE_SERVER_TAG = "Available";
    private static final String LOG_TAG = ScanFragment.class.getSimpleName();
    public static String SCANNED_DEVICE = "scanned_device";
    public static String SCANNED_USER = "scanned_user";
    public static String DEVICE_NAME_TAG = "deviceName";
    public static String SERIAL_NUMBER_TAG = "serialNumber";
    public static String USER_NAME_TAG = "userName";


    private EditText userName;
    private TextView deviceName, serialNumber;
    private Button checkOutButton;
    private Button checkInButton;
    private Firebase firebase;
    private ImageButton scanDevice;

    public ScanFragment() {

    }

    public static ScanFragment newInstance(String scannedDevice) {
        ScanFragment f = new ScanFragment();

        Bundle args = new Bundle();
        args.putString(SCANNED_DEVICE, scannedDevice);
        f.setArguments(args);
        return f;
    }

    public String getScannedDevice() {
        return getArguments().getString(SCANNED_DEVICE);
    }

    public String getScannedUser() {
        return getArguments().getString(SCANNED_USER);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DEVICE_NAME_TAG, deviceName.getText().toString());
        outState.putString(SERIAL_NUMBER_TAG, serialNumber.getText().toString());
        outState.putString(USER_NAME_TAG, userName.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            deviceName.setText(savedInstanceState.getString(DEVICE_NAME_TAG));
            serialNumber.setText(savedInstanceState.getString(SERIAL_NUMBER_TAG));
            userName.setText(savedInstanceState.getString(USER_NAME_TAG));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_scan, container, false);
        initializeElements(rootView);

        scanDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan(view);
            }
        });


        Bundle arguments = getArguments();
        if (arguments != null) {
            if (arguments.getString(SCANNED_DEVICE) != null) {
                if (arguments.getString(SCANNED_DEVICE).contains(DEVICE_NAME_TAG)) {
                    try {
                        JSONObject jObj = new JSONObject(getScannedDevice());
                        deviceName.setText(jObj.getString(DEVICE_NAME_TAG));
                        serialNumber.setText(jObj.getString(SERIAL_NUMBER_TAG));
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Scanned text is not a proper JSON. Exception caught");
                        showErrorSnackBar(rootView, getString(R.string.invalidQRMessage));

                    }
                } else {
                    showErrorSnackBar(rootView, getString(R.string.invalidQRMessage));
                }
            } else if (arguments.getString(SCANNED_USER) != null) {
                if (arguments.getString(SCANNED_USER).contains(USER_NAME_TAG)) {
                    try {
                        JSONObject jObj = new JSONObject(getScannedUser());
                        userName.setText(jObj.getString(USER_NAME_TAG));
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Scanned text is not a proper JSON. Exception caught");
                        showErrorSnackBar(rootView, getString(R.string.invalidQRMessage));

                    }
                } else {
                    showErrorSnackBar(rootView, getString(R.string.invalidQRMessage));
                }
            } else {
                showErrorSnackBar(rootView, getString(R.string.invalidQRMessage));
            }
        }

        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebase.getAuth() != null) {
                    checkOutDevice(deviceName.getText().toString().trim(), serialNumber.getText().toString().trim(), userName.getText().toString().trim());
                } else {
                    showErrorSnackBar(view, getString(R.string.error_message_checkout_without_sign_in));
                }
            }
        });

        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebase.getAuth() != null) {
                    checkInDevice(deviceName.getText(), serialNumber.getText());
                } else {
                    showErrorSnackBar(view, getString(R.string.error_message_checkin_without_sign_in));
                }
            }
        });
        Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL);
        if (firebaseRef.getAuth() != null) {
            Firebase listNameRef = new Firebase(Constants.FIREBASE_URL).child(firebaseRef.getAuth().getUid()).child(Constants.FIREBASE_LOCATION_CURRENT_LIST);
            listNameRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    FetchDevicesStatusTask fetchArtistsTask = new FetchDevicesStatusTask(getActivity());
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
        }
        return rootView;
    }

    private void checkOutDevice(String deviceName, String serialNumber, String userName) {
        HashMap<String, Object> dateLastChangedObj = new HashMap<String, Object>();
        Map<String, String> date = ServerValue.TIMESTAMP;
        dateLastChangedObj.put("date", date);
        if (deviceName != null && deviceName != "" && serialNumber != null && serialNumber != "" && userName != null && userName.length() != 0) {
            Device deviceObj = new Device(deviceName, serialNumber, userName, dateLastChangedObj);
            firebase.child(firebase.getAuth().getUid()).child(Constants.FIREBASE_LOCATION_CURRENT_LIST).child(serialNumber.toString()).setValue(deviceObj);
            showErrorSnackBar(getString(R.string.checkout_confirmation));

        } else {
            showErrorSnackBar(getView(), getString(R.string.error_message_devicename_serial_user_not_null));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        updateWidgetData();

    }

    private void checkInDevice(CharSequence deviceName, CharSequence serialNumber) {
        HashMap<String, Object> dateLastChangedObj = new HashMap<String, Object>();
        Map<String, String> date = ServerValue.TIMESTAMP;
        dateLastChangedObj.put("date", date);
        if (deviceName != null && deviceName != "" && serialNumber != null && serialNumber != "") {
            Device deviceObj = new Device(deviceName.toString(), serialNumber.toString(), AVAILABLE_SERVER_TAG, dateLastChangedObj);
            firebase.child(firebase.getAuth().getUid()).child(Constants.FIREBASE_LOCATION_CURRENT_LIST).child(serialNumber.toString()).setValue(deviceObj);
            showErrorSnackBar(getString(R.string.checkin_confirmation));
        }
    }

    private void updateWidgetData() {
        Intent dataUpdatedIntent = new Intent(FetchDevicesStatusTask.ACTION_DATA_UPDATED);
        getActivity().sendBroadcast(dataUpdatedIntent);
    }

    private void initializeElements(View rootView) {
        userName = (EditText) rootView.findViewById(R.id.list_item_userName);

        deviceName = (TextView) rootView.findViewById(R.id.list_item_deviceName);
        serialNumber = (TextView) rootView.findViewById(R.id.serialNumber);
        checkOutButton = (Button) rootView.findViewById(R.id.checkOut);
        checkInButton = (Button) rootView.findViewById(R.id.checkIn);
        scanDevice = (ImageButton) rootView.findViewById(R.id.scanDevice);
        firebase = new Firebase(Constants.FIREBASE_URL);
    }


    public void scan(View v) {
        IntentIntegrator zxingIntegrator = new IntentIntegrator(this.getActivity());
        zxingIntegrator.initiateScan();
    }


}
