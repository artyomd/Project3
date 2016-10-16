package artyomd.project3;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by artyomd on 4/26/16.
 */
public class ImageAdapter extends RecyclerView.Adapter {
    private List<Bitmap> data;
    private UpdateDelegate delegate;

    public ImageAdapter(UpdateDelegate delegate) {
        this.delegate = delegate;
    }

    public void setData(List<Bitmap> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        vh = new ImageViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (data != null) {
            Bitmap bitmap = data.get(position);
            ((ImageViewHolder) holder).bmp = bitmap;
            ((ImageViewHolder) holder).imgView.setImageBitmap(bitmap);

        }

    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        return 0;
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgView;
        public Bitmap bmp;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imgView = (ImageView) itemView.findViewById(R.id.imageView);
            imgView.setLayoutParams(new GridView.LayoutParams(500, 500));
            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delegate.updateView(bmp);
                }
            });
        }
    }

}
