package dataming.com;

/**
 * Created by Ivanf on 12/12/2017.
 */
import android.app.ProgressDialog;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @VisibleForTesting
    public ProgressDialog mProgressDialog, mProgressDialog2;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading_app));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void showProgressDialog2() {
        if (mProgressDialog2 == null) {
            mProgressDialog2 = new ProgressDialog(this);
            mProgressDialog2.setMessage(getString(R.string.welcomeing_user));
            mProgressDialog2.setIndeterminate(true);
        }

        mProgressDialog2.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}
