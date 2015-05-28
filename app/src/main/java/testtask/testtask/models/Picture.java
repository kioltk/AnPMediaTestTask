package testtask.testtask.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jesus Christ. Amen.
 */
public class Picture {
    public int id;
    @SerializedName("webformatHeight")
    public int height;
    @SerializedName("webformatWidth")
    public int width;
    @SerializedName("webformatURL")
    public String url;
}
