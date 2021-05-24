package au.edu.federation.itech3107.fedunimillionaire30360914.helpers;

import android.content.Context;
import android.net.ConnectivityManager;

public class CheckNetwork {

    private Context mContext;

    public CheckNetwork(Context context) {
        this.mContext = context;
    }

    /**
     * Check network connection
     * reference: https://stackoverflow.com/a/9570292
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}
