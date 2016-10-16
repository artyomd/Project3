package artyomd.project3;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by artyomd on 4/27/16.
 */
public interface UpdateDelegate {
    void updateView(Bitmap bmp);
    void asyncComplete(List<Bitmap> data);
}
