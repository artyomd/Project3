package artyomd.project3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by artyomd on 4/15/16.
 */
public class LoadTask extends AsyncTask<List<String>, Void, List<Bitmap>> {

    private UpdateDelegate delegate;

    public LoadTask(UpdateDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<Bitmap> doInBackground(List<String>... params) {
        List<Bitmap> bmps = new ArrayList<>();
        for (String s : params[0])
            bmps.add(decodeSampledBitmapFromResource(s, 500, 500));
        return bmps;
    }

    @Override
    protected void onPostExecute(List<Bitmap> items) {
        super.onPostExecute(items);
        delegate.asyncComplete(items);
    }

    public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}