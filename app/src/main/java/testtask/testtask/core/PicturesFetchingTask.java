package testtask.testtask.core;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import testtask.testtask.models.Picture;
import testtask.testtask.storage.StorageManager;

/**
 * Created by Jesus Christ. Amen.
 */
public abstract class PicturesFetchingTask extends AsyncTask<Object,Void,Object> {
    private static final String LOG_TAG = "Fethcing Task";
    private static final String serverUrl = "http://pixabay.com/api/" ;
    protected String methodName = "";
    private NameValuePair fileArgument = null;
    List<NameValuePair> arguments;
    public PicturesFetchingTask(String query){
        arguments = new ArrayList<>();
        addArgument("q", query);
        addArgument("username","kioltk0");
        addArgument("key","a65c8cacaa9c3dcf5e74");
        addArgument("image_type","photo");
        addArgument("per_page", 50);

    }

    public void addArgument(String argumentKey, int argumentValue) {
        arguments.add(new BasicNameValuePair(argumentKey, String.valueOf(argumentValue)));
    }

    public void addArgument(String argumentKey, String argumentValue) {
        arguments.add(new BasicNameValuePair(argumentKey, argumentValue));
    }

    @Override
    protected Object doInBackground(Object... params) {
        try {
            String url = serverUrl + methodName;

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpRequestBase request;
            String args = URLEncodedUtils.format(arguments, "utf-8");
            url += "?" + args;
            request = new HttpGet(url);

            Log.i(LOG_TAG, "Executing request to url: " + url);
            String argumentsLogging = "";
            for (NameValuePair argument : arguments) {
                argumentsLogging += argument.getName() + " = " + argument.getValue() + "\n";
            }
            Log.i(LOG_TAG,"Arguments: " + argumentsLogging);

            HttpResponse httpResponse = httpClient.execute(request);
            HttpEntity httpEntity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(httpEntity);
            Log.i(LOG_TAG,"Server response " + responseString);
            return parseAndSave(responseString);

        }
        catch (Exception exp){
            return exp;
        }
    }

    protected Object parseAndSave(String json) throws Exception {
        ArrayList<Picture> pictures = new Gson().fromJson(new JSONObject(json).getString("hits"), new TypeToken<ArrayList<Picture>>() {
        }.getType());
        new StorageManager().clearAndStore(pictures);
        return pictures;
    }

    @Override
    protected void onPostExecute(Object o) {
        if(o instanceof Exception){
            Exception exp = (Exception) o;
            exp.printStackTrace();
            onError(exp);
        }else{
            onSuccess((ArrayList<Picture>) o);
        }
    }

    protected abstract void onSuccess(ArrayList<Picture> result);

    protected abstract void onError(Exception exp);

    public void start() {
        execute();
    }
}
