package testtask.testtask.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import testtask.testtask.R;
import testtask.testtask.models.Picture;

/**
 * Created by Jesus Christ. Amen.
 */
public class PicturesAdapter extends RecyclerView.Adapter {
    private final ArrayList<Picture> pictures;

    public PicturesAdapter(ArrayList<Picture> pictures) {
        this.pictures = pictures;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PictureHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Picture picture = pictures.get(position);
        PictureHolder pictureHolder = (PictureHolder) holder;
        pictureHolder.bind(picture);
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }
}
