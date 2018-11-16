package com.arcsoft.sdk_demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import java.io.File;

/**
 * Created by gqj3375 on 2017/4/28.
 */

public class Application extends android.app.Application {
	private final String TAG = this.getClass().toString();
	FaceDB mFaceDB;
	Uri mImage;

	@Override
	public void onCreate() {
		super.onCreate();
        String path =this.getExternalCacheDir().getPath() ;//this.getCacheDir().getPath();//

        File file = new File(path + "/face.txt");
        if(!file.exists())
        {
            loadFaceInfo(path);
        }


        mFaceDB = new FaceDB(path);
        mImage = null;

	}

	private void loadFaceInfo(String path)
    {

        Utils.copyFilesFromRaw(this,R.raw.face,"face.txt",path);
        Utils.copyFilesFromRaw(this,R.raw.irving,"irving.data",path);
        Utils.copyFilesFromRaw(this,R.raw.jason,"jason.data",path);
        Utils.copyFilesFromRaw(this,R.raw.kira,"kira.data",path);
        Utils.copyFilesFromRaw(this,R.raw.rock,"rock.data",path);


        Utils.copyFilesFromRaw(this,R.raw.gavier,"gavier.data",path);
        Utils.copyFilesFromRaw(this,R.raw.jack,"jack.data",path);
        Utils.copyFilesFromRaw(this,R.raw.jesse,"jesse.data",path);
        Utils.copyFilesFromRaw(this,R.raw.jojo,"jojo.data",path);

        Utils.copyFilesFromRaw(this,R.raw.kevin,"kevin.data",path);
        Utils.copyFilesFromRaw(this,R.raw.mark,"mark.data",path);
        Utils.copyFilesFromRaw(this,R.raw.nina,"nina.data",path);
        Utils.copyFilesFromRaw(this,R.raw.sissi,"sissi.data",path);

        Utils.copyFilesFromRaw(this,R.raw.steven,"steven.data",path);
        Utils.copyFilesFromRaw(this,R.raw.tomi,"tomi.data",path);
        Utils.copyFilesFromRaw(this,R.raw.vince,"vince.data",path);
        Utils.copyFilesFromRaw(this,R.raw.vinson,"vinson.data",path);
        Utils.copyFilesFromRaw(this,R.raw.white,"white.data",path);

        Utils.copyFilesFromRaw(this,R.raw.faye,"faye.data",path);
        Utils.copyFilesFromRaw(this,R.raw.charles,"charles.data",path);





    }

	public void setCaptureImage(Uri uri) {
		mImage = uri;
	}

	public Uri getCaptureImage() {
		return mImage;
	}

	/**
	 * @param path
	 * @return
	 */
	public static Bitmap decodeImage(String path) {
		Bitmap res;
		try {
			ExifInterface exif = new ExifInterface(path);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

			BitmapFactory.Options op = new BitmapFactory.Options();
			op.inSampleSize = 1;
			op.inJustDecodeBounds = false;
			//op.inMutable = true;
			res = BitmapFactory.decodeFile(path, op);
			//rotate and scale.
			Matrix matrix = new Matrix();

			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				matrix.postRotate(90);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
				matrix.postRotate(180);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				matrix.postRotate(270);
			}

			Bitmap temp = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
			Log.d("com.arcsoft", "check target Image:" + temp.getWidth() + "X" + temp.getHeight());

			if (!temp.equals(res)) {
				res.recycle();
			}
			return temp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
