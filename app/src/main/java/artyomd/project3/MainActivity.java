package artyomd.project3;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UpdateDelegate {

    private final int PERMISSIONS_READ_EXTERNAL_STORAGE = 69;
    private FloatingActionButton fabC;
    private FloatingActionButton fabR;
    private FloatingActionButton fabCancel;
    private DrawingView dView;
    private RecyclerView rView;
    private ImageAdapter adapter;
    private FloatingActionMenu fabM;
    private int height;
    private int width;
    private boolean isExpended = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resolvePermissionAndInitView();
    }

    private void resolvePermissionAndInitView() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_READ_EXTERNAL_STORAGE);
        } else {
            initView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initView();
                } else {
                    this.finish();
                }
            }
        }
    }

    public void initView() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        fabM = (FloatingActionMenu) findViewById(R.id.view);
        fabC = (FloatingActionButton) findViewById(R.id.circle);
        fabR = (FloatingActionButton) findViewById(R.id.image);
        fabCancel = (FloatingActionButton) findViewById(R.id.cancel_button);
        dView = (DrawingView) findViewById(R.id.dView);
        rView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new ImageAdapter(this);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        rView.setLayoutManager(mGridLayoutManager);
        rView.setAdapter(adapter);
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        rView.addItemDecoration(decoration);
        rView.setItemAnimator(new DefaultItemAnimator());
        rView.setLayoutParams(new RelativeLayout.LayoutParams(width, height / 2));
        rView.setTranslationY(height / 2);


        assert fabR != null;
        fabR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpended) {
                    rView.animate().translationYBy(-height / 8).start();
                    fabM.animate().translationYBy(-height / 2).start();
                    dView.animate().translationYBy(-height / 2).start();
                    isExpended = true;
                    runAsyncTask();
                }
                fabM.close(true);
            }
        });
        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpended) {
                    rView.animate().translationYBy(height / 8).start();
                    fabM.animate().translationYBy(height / 2).start();
                    dView.animate().translationYBy(height / 2).start();
                    isExpended = false;
                }
            }
        });
        fabC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpended) {
                    rView.animate().translationYBy(height / 8).start();
                    fabM.animate().translationYBy(height / 2).start();
                    dView.animate().translationYBy(height / 2).start();
                    isExpended = false;
                }
                fabM.close(true);
                dView.setDrawingMode(true);
            }
        });
    }

    private void runAsyncTask() {
        new LoadTask(this).execute(getImagesPath());
    }

    public ArrayList<String> getImagesPath() {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        Cursor cursor;
        int column_index_data;
        String PathOfImage;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = this.getContentResolver().query(uri, projection, null,
                null, null);

        assert cursor != null;
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);
            listOfAllImages.add(PathOfImage);
        }
        cursor.close();
        return listOfAllImages;
    }

    @Override
    public void asyncComplete(List<Bitmap> data) {
        this.adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void updateView(Bitmap bmp) {
        rView.animate().translationYBy(height / 8).start();
        fabM.animate().translationYBy(height / 2).start();
        dView.animate().translationYBy(height / 2).start();
        dView.setBmp(bmp);
        dView.setDrawingMode(false);
        isExpended = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                openCaptureDialog();
                break;
            case R.id.grid:
                dView.setShare(!dView.isShare());
                if (!dView.isShare()) {
                    item.setTitle("Grid:ON");
                } else {
                    item.setTitle("Grid:OFF");
                }
                dView.invalidate();
                break;
            default:
                break;
        }

        return true;
    }

    private void openCaptureDialog() {
        boolean grid = !dView.isShare();
        if (grid) {
            dView.setShare(true);
            dView.invalidate();
        }
        this.dView.buildDrawingCache();
        Bitmap bitmap = this.dView.getDrawingCache();
        ShareImage(bitmap);
        if (grid) {
            dView.setShare(false);
            dView.invalidate();
        }
    }

    protected void ShareImage(Bitmap bmp) {
        try {

            File cachePath = new File(getApplicationContext().getCacheDir(), "images");
            cachePath.mkdirs();
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File imagePath = new File(getApplicationContext().getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "artyomd.project3", newFile);

        if (contentUri != null) {
            startNotification();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Choose an app to share"));


        }
    }

    private void startNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.side_nav_bar)
                        .setContentTitle("Project 3")
                        .setContentText("Export Completed");
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, mBuilder.build());
    }
}
