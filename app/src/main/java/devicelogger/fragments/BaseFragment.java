package devicelogger.fragments;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by santosh on 5/21/16.
 */
public abstract class BaseFragment extends Fragment {
    protected void showErrorSnackBar(String s) {
        Snackbar.make(getView(), s, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
        //  Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    protected void showErrorSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
