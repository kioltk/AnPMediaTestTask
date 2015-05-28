package testtask.testtask.core;

import android.os.AsyncTask;

import java.util.ArrayList;

import testtask.testtask.models.Picture;
import testtask.testtask.storage.StorageManager;

/**
 * Created by Jesus Christ. Amen.
 */
public abstract class CacheLoadingTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] params) {
        try {
            return new StorageManager().loadPictures();
        } catch(Exception exp){
            return exp;
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        if(o instanceof Exception){
            onError((Exception) o);
        } else {
            onSuccess((ArrayList<Picture>) o);
        }
    }

    protected abstract void onSuccess(ArrayList<Picture> result);

    protected abstract void onError(Exception exp);
}
