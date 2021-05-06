package au.edu.federation.itech3107.fedunimillionaire30360914.helpers;

import android.content.Context;
import android.net.ConnectivityManager;

public class CheckNetwork {

    private Context context;

    public CheckNetwork(Context context) {
        this.context = context;
    }

    /**
     * reference: https://stackoverflow.com/a/9570292
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}
