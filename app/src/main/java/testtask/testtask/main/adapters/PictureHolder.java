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
        // First of all we dispatch the bitmap, cause we don't want to show an old image before
        // the new one will be attached.
        pictureView.setImageBitmap(null);

        // We have resize an pictureView cause accordingly to picture size ratio. It is necessary
        // because adapter will move the items if it resizes dynamically.
        int itemViewWidth = itemView.getWidth();
        float pictureRatio = (float) picture.width / (float) picture.height;
        int itemViewHeight = (int) (itemViewWidth / pictureRatio);
        // if item is created it will not return height cause it is not meausred, so we dont have to
        // resize it at start.
        // However we can define the size at start in case of showing right items size.
        if (itemViewHeight > 0) {
            ViewGroup.LayoutParams params = pictureView.getLayoutParams();
            params.height = itemViewHeight;
            pictureView.requestLayout();
            pictureView.setLayoutParams(params);
        }
        Log.d(TAG, "PictureRatio=" + pictureRatio + " View={width=" + itemViewWidth + ", height=" + itemViewHeight + "}, Picutre={width=" + picture.width + ", height=" + picture.height + "}");
        // Dispatch old binding.
        ImageLoader.getInstance().cancelDisplayTask(pictureView);
        // Trying to load from memory.
        if (ImageLoader.getInstance().getMemoryCache().keys().contains(picture.url)) {
            Bitmap bitmap = ImageLoader.getInstance().getMemoryCache().get(picture.url);
            pictureView.setImageBitmap(bitmap);
            Log.d(TAG, "Picture loaded from memory");
        } else {
            // Or from cache?
            File imageFile = ImageLoader.getInstance().getDiskCache().get(picture.url);
            if (imageFile != null) {
                ImageLoader.getInstance().displayImage("file://" + imageFile.getPath(), pictureView);
                Log.d(TAG, "Picture loaded from disk");
            } else {
                // Okay, then lets download it and save.
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .build();
                ImageLoader.getInstance().displayImage(picture.url, pictureView, options);
                Log.d(TAG, "Picture loaded from web");
            }
        }
    }
}
