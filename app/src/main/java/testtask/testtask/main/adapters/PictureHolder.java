package testtask.testtask.main.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import testtask.testtask.R;
import testtask.testtask.models.Picture;

/**
 * Created by Jesus Christ. Amen.
 */
public class PictureHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "PictureHolder";
    private final ImageView pictureView;

    public PictureHolder(View itemView) {
        super(itemView);
        this.pictureView = (ImageView) itemView.findViewById(R.id.picture);
        pictureView.setAdjustViewBounds(true);
    }

    public void bind(final Picture picture) {
        pictureView.setImageBitmap(null);
        int itemViewWidth = itemView.getWidth();
        float pictureRatio = (float) picture.width / (float) picture.height;
        int itemViewHeight = (int) (itemViewWidth / pictureRatio);

        if(itemViewHeight>0) {

            ViewGroup.LayoutParams params = pictureView.getLayoutParams();
            params.height = itemViewHeight;
            pictureView.requestLayout();
            pictureView.setLayoutParams(params);
        }
        Log.d(TAG, "PictureRatio="+pictureRatio+" View={width=" + itemViewWidth + ", height="+itemViewHeight+"}, Picutre={width="+picture.width+", height="+picture.height +"}");

        ImageLoader.getInstance().cancelDisplayTask(pictureView);

        if (ImageLoader.getInstance().getMemoryCache().keys().contains(picture.url)) {
            Bitmap bitmap = ImageLoader.getInstance().getMemoryCache().get(picture.url);
            pictureView.setImageBitmap(bitmap);
        } else {

            File imageFile = ImageLoader.getInstance().getDiskCache().get(picture.url);
            if (imageFile!=null) {
                ImageLoader.getInstance().displayImage("file://" + imageFile.getPath(), pictureView);
            } else {
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .build();
                ImageLoader.getInstance().displayImage(picture.url, pictureView, options);
            }
        }
    }
}
