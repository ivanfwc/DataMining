package dataming.com;

import android.os.Build;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static dataming.com.Config.MY_SERVER_URL;

public class SetFirstTimeRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = MY_SERVER_URL + "user_login2.php";
    private Map<String, String> params;
    private FirebaseAuth mAuth;

    public SetFirstTimeRequest(String Name, String Email, String UserId, Response.Listener<String> listener) {
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        mAuth = FirebaseAuth.getInstance();
        params = new HashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            params.put("Name", Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName()).trim());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            params.put("Email", Objects.requireNonNull(mAuth.getCurrentUser().getEmail()).trim());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            params.put("UserId", Objects.requireNonNull(mAuth.getCurrentUser()).getUid().trim());
        }
        //params.put("User_Name", MainActivity.global_Username);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
