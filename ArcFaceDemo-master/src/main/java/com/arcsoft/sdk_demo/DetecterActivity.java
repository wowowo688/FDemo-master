package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.content.ContentUris;

import android.content.Context;

import android.database.Cursor;

import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;

import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import android.widget.ListView;
import android.widget.SimpleAdapter;


/*import com.arcsoft.ageestimation.ASAE_FSDKAge;
import com.arcsoft.ageestimation.ASAE_FSDKEngine;
import com.arcsoft.ageestimation.ASAE_FSDKError;
import com.arcsoft.ageestimation.ASAE_FSDKFace;
import com.arcsoft.ageestimation.ASAE_FSDKVersion;*/
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
/*import com.arcsoft.genderestimation.ASGE_FSDKEngine;
import com.arcsoft.genderestimation.ASGE_FSDKError;
import com.arcsoft.genderestimation.ASGE_FSDKFace;
import com.arcsoft.genderestimation.ASGE_FSDKGender;
import com.arcsoft.genderestimation.ASGE_FSDKVersion;*/
import com.guo.android_extend.java.AbsLoop;
/*import com.guo.android_extend.java.ExtByteArrayOutputStream;*/
import com.guo.android_extend.tools.CameraHelper;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView.OnCameraListener;
/*import com.guo.android_extend.widget.RotateRunable;*/

/*import java.io.IOException;*/
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gqj3375 on 2017/4/28.
 */

public class DetecterActivity extends Activity implements OnCameraListener, View.OnTouchListener, Camera.AutoFocusCallback {
	private final String TAG = this.getClass().getSimpleName();

	private int mWidth, mHeight, mFormat;
	private CameraSurfaceView mSurfaceView;
	private CameraGLSurfaceView mGLSurfaceView;
	private Camera mCamera;



	AFT_FSDKVersion version = new AFT_FSDKVersion();
	AFT_FSDKEngine engine = new AFT_FSDKEngine();
	//ASAE_FSDKVersion mAgeVersion = new ASAE_FSDKVersion();
	//ASAE_FSDKEngine mAgeEngine = new ASAE_FSDKEngine();

	//ASGE_FSDKVersion mGenderVersion = new ASGE_FSDKVersion();
	//ASGE_FSDKEngine mGenderEngine = new ASGE_FSDKEngine();
	List<AFT_FSDKFace> result = new ArrayList<>();
	//List<ASAE_FSDKAge> ages = new ArrayList<>();
	//List<ASGE_FSDKGender> genders = new ArrayList<>();

	List<AFT_FSDKFace> resultPerson = Collections.synchronizedList(new ArrayList<AFT_FSDKFace>());

	int mCameraID;
	int mCameraRotate;
	boolean mCameraMirror;
	byte[] mImageNV21 = null;
	FRAbsLoop mFRAbsLoop = null;
	AFT_FSDKFace mAFT_FSDKFace = null;
	Handler mHandler;
	boolean isPostted = false;

	Runnable hide = new Runnable() {
		@Override
		public void run() {
			//mTextView.setAlpha(0.2f);
			//mImageView.setImageAlpha(10);

            listViewPerson.setAlpha(0.2f);
			isPostted = false;
		}
	};



    /*AFR_FSDKVersion versionAFR;
    AFR_FSDKEngine engineAFR;
    AFR_FSDKFace resultAFR;
    List<FaceDB.FaceRegist> mResgist ;
    private void initFaceEngine()
    {
         versionAFR = new AFR_FSDKVersion();
         engineAFR = new AFR_FSDKEngine();
         resultAFR= new AFR_FSDKFace();

         mResgist = ((Application)DetecterActivity.this.getApplicationContext()).mFaceDB.mRegister;

         AFR_FSDKError error = engineAFR.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
         Log.d(TAG, "AFR_FSDK_InitialEngine = " + error.getCode());
         error = engineAFR.AFR_FSDK_GetVersion(versionAFR);
         Log.d(TAG, "FR=" + versionAFR.toString() + "," + error.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
    }*/

    //public boolean isLoop = false;
	class FRAbsLoop extends AbsLoop {

		AFR_FSDKVersion version = new AFR_FSDKVersion();
		AFR_FSDKEngine engine = new AFR_FSDKEngine();
		AFR_FSDKFace resultAFR= new AFR_FSDKFace();
		List<FaceDB.FaceRegist> mResgist = ((Application)DetecterActivity.this.getApplicationContext()).mFaceDB.mRegister;
		//List<ASAE_FSDKFace> face1 = new ArrayList<>();
		//List<ASGE_FSDKFace> face2 = new ArrayList<>();
		@Override
		public void setup() {
			AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
			Log.d(TAG, "AFR_FSDK_InitialEngine = " + error.getCode());
			error = engine.AFR_FSDK_GetVersion(version);
			Log.d(TAG, "FR=" + version.toString() + "," + error.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
		}

		@Override
		public void loop() {
			if (mImageNV21 != null) {
                personArray.clear();
			    for(AFT_FSDKFace aftFace: resultPerson)
                {

                    mAFT_FSDKFace = aftFace.clone();

                    //final int rotate = mCameraRotate;
                    long time = System.currentTimeMillis();
                    AFR_FSDKError error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), resultAFR);
                    Log.e(TAG, "AFR_FSDK_ExtractFRFeature cost :" + (System.currentTimeMillis() - time) + "ms");
                    //Log.e(TAG, "Face=" + resultAFR.getFeatureData()[0] + "," + resultAFR.getFeatureData()[1] + "," + resultAFR.getFeatureData()[2] + "," + error.getCode());
                    AFR_FSDKMatching score = new AFR_FSDKMatching();
                    float max = 0.0f;
                    String name = "未知"+System.currentTimeMillis();
                    for (FaceDB.FaceRegist fr : mResgist) {
                        for (AFR_FSDKFace face : fr.mFaceList) {
                            error = engine.AFR_FSDK_FacePairMatching(resultAFR, face, score);
                            //Log.d(TAG,  "Score:" + score.getScore() + ", AFR_FSDK_FacePairMatching=" + error.getCode());
                            if (max < score.getScore()) {
                                max = score.getScore();
                                name = fr.mName;
                            }
                        }
                    }

                    //age & gender
                    /*face1.clear();
                    face2.clear();
                    face1.add(new ASAE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
                    face2.add(new ASGE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
                    ASAE_FSDKError error1 = mAgeEngine.ASAE_FSDK_AgeEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face1, ages);
                    ASGE_FSDKError error2 = mGenderEngine.ASGE_FSDK_GenderEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face2, genders);
                    Log.d(TAG, "ASAE_FSDK_AgeEstimation_Image:" + error1.getCode() + ",ASGE_FSDK_GenderEstimation_Image:" + error2.getCode());
                    Log.d(TAG, "age:" + ages.get(0).getAge() + ",gender:" + genders.get(0).getGender());
                    final String age = ages.get(0).getAge() == 0 ? "年龄未知" : ages.get(0).getAge() + "岁";
                    final String gender = genders.get(0).getGender() == -1 ? "性别未知" : (genders.get(0).getGender() == 0 ? "男" : "女");

                    //crop
                    byte[] data = mImageNV21;
                    YuvImage yuv = new YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null);
                    ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
                    yuv.compressToJpeg(mAFT_FSDKFace.getRect(), 80, ops);
                    final Bitmap bmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);
                    try {
                        ops.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    if (max < 0.6f || max == 0.6f)
                    {
                        name = "未知"+System.currentTimeMillis() ;
                    }

                    final String mNameShow = name;
                    Log.d(TAG, "fit Score:" + max + ", NAME:" + name);
                    mHandler.removeCallbacks(hide);
                    mHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            listViewPerson.setAlpha(1.0f);
                            Person person = new Person();
                            person.setAge("年龄");
                            switch(mNameShow)
                            {
                                case "irving":
                                    person.setImage(R.drawable.irving);
                                    break;
                                case "kira":
                                    person.setImage(R.drawable.kira);
                                    break;
                                case "jason":
                                    person.setImage(R.drawable.jason);
                                    break;
                                case "gavier":
                                    person.setImage(R.drawable.gavier);
                                    break;
                                case "jack":
                                    person.setImage(R.drawable.jack);
                                    break;
                                case "jesse":
                                    person.setImage(R.drawable.jesse);
                                    break;
                                case "jojo":
                                    person.setImage(R.drawable.jojo);
                                    break;
                                case "kevin":
                                    person.setImage(R.drawable.kevin);
                                    break;
                                case "mark":
                                    person.setImage(R.drawable.mark);
                                    break;
                                case "nina":
                                    person.setImage(R.drawable.nina);
                                    break;
                                case "sissi":
                                    person.setImage(R.drawable.sissi);
                                    break;
                                case "steven":
                                    person.setImage(R.drawable.steven);
                                    break;
                                case "tomi":
                                    person.setImage(R.drawable.tomi);
                                    break;
                                case "vince":
                                    person.setImage(R.drawable.vince);
                                    break;
                                case "vinson":
                                    person.setImage(R.drawable.vinson);
                                    break;
                                case "white":
                                    person.setImage(R.drawable.white);
                                    break;
                                case "faye":
                                    person.setImage(R.drawable.faye);
                                    break;
                                case "charles":
                                    person.setImage(R.drawable.charles);
                                    break;
                                default:
                                    person.setImage(R.drawable.unknow);
                                    break;
                            }

                            person.setName(mNameShow);
                            person.setSex("性别");

                            if(!personArrayContainIt(person))
                            {
                                personArray.add(person);

                            }
                        }
                    });
                }


				mImageNV21 = null;
                DetecterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updatePerson();
                    }
                });

			}

		}

		private boolean personArrayContainIt(Person p)
        {
            for(Person person:personArray)
            {
                if(person.equals(p))
                {
                    return true;
                }
            }
            return false;
        }

		@Override
		public void over() {
			AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();
			Log.d(TAG, "AFR_FSDK_UninitialEngine : " + error.getCode());
		}
	}

	//private TextView mTextView;
	//private TextView mTextView1;
	//private ImageView mImageView;

    private ListView listViewPerson;
    private SimpleAdapter listAdapter;
    private List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();

    private ArrayList<Person> personArray = new ArrayList<Person>();

    private void initAdaper()
    {
        listViewPerson = (ListView)findViewById(R.id.personListView);

        int[] ins = {R.id.personImage};
        String[] strings = {"image"};

        listAdapter = new SimpleAdapter(this,dataList,R.layout.person_sample,strings,ins);

        listViewPerson.setAdapter(listAdapter);

        listAdapter.notifyDataSetChanged();

    }

    private void updatePerson()
    {
        int[] ins = {R.id.personImage};
        String[] strings = {"image"};
        dataList.clear();

        for(Person person:personArray)
        {
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("image",person.getImage());

            dataList.add(map);
        }
        listAdapter = new SimpleAdapter(this,dataList,R.layout.person_sample,strings,ins);

        listViewPerson.setAdapter(listAdapter);

        listAdapter.notifyDataSetChanged();
    }

    private boolean personArrayContainIt(Person p)
    {
        for(Person person:personArray)
        {
            if(person.equals(p))
            {
                return true;
            }
        }
        return false;
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);



		mCameraID = getIntent().getIntExtra("Camera", 0) == 0 ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;
		mCameraRotate = 0;
		mCameraMirror = false;
		mWidth = getWindowManager().getDefaultDisplay().getWidth();
		mHeight = getWindowManager().getDefaultDisplay().getHeight();
		mFormat = ImageFormat.NV21;
		mHandler = new Handler();

		setContentView(R.layout.activity_camera);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);

		mGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.glsurfaceView);
		mGLSurfaceView.setOnTouchListener(this);
		mSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
		mSurfaceView.setOnCameraListener(this);
		mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, mCameraMirror, mCameraRotate);
		mSurfaceView.debug_print_fps(true, false);

		//snap
		//mTextView = (TextView) findViewById(R.id.textView);
		//mTextView.setText("");
		//mTextView1 = (TextView) findViewById(R.id.textView1);
		//mTextView1.setText("");

		//mImageView = (ImageView) findViewById(R.id.imageView);


		AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
		Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.getCode());
		err = engine.AFT_FSDK_GetVersion(version);
		Log.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.getCode());

		/*ASAE_FSDKError error = mAgeEngine.ASAE_FSDK_InitAgeEngine(FaceDB.appid, FaceDB.age_key);
		Log.d(TAG, "ASAE_FSDK_InitAgeEngine =" + error.getCode());
		error = mAgeEngine.ASAE_FSDK_GetVersion(mAgeVersion);
		Log.d(TAG, "ASAE_FSDK_GetVersion:" + mAgeVersion.toString() + "," + error.getCode());

		ASGE_FSDKError error1 = mGenderEngine.ASGE_FSDK_InitgGenderEngine(FaceDB.appid, FaceDB.gender_key);
		Log.d(TAG, "ASGE_FSDK_InitgGenderEngine =" + error1.getCode());
		error1 = mGenderEngine.ASGE_FSDK_GetVersion(mGenderVersion);
		Log.d(TAG, "ASGE_FSDK_GetVersion:" + mGenderVersion.toString() + "," + error1.getCode());*/

		initAdaper();


		mFRAbsLoop = new FRAbsLoop();
		mFRAbsLoop.start();


	}

    //private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
    //private static final int REQUEST_CODE_IMAGE_OP = 2;
    //private static final int REQUEST_CODE_OP = 3;

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE_OP && resultCode == RESULT_OK) {
            Uri mPath = data.getData();
            String file = getPath(mPath);
            Bitmap bmp = Application.decodeImage(file);
            if (bmp == null || bmp.getWidth() <= 0 || bmp.getHeight() <= 0 ) {
                Log.e(TAG, "error");
            } else {
                Log.i(TAG, "bmp [" + bmp.getWidth() + "," + bmp.getHeight());
            }
            startRegister(bmp, file);
        } else if (requestCode == REQUEST_CODE_OP) {
            Log.i(TAG, "RESULT =" + resultCode);
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();
            String path = bundle.getString("imagePath");
            Log.i(TAG, "path="+path);
        } else if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
            Uri mPath = ((Application)(DetecterActivity.this.getApplicationContext())).getCaptureImage();
            String file = getPath(mPath);
            Bitmap bmp = Application.decodeImage(file);
            startRegister(bmp, file);
        }
    }*/

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mFRAbsLoop.shutdown();


		AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
		Log.d(TAG, "AFT_FSDK_UninitialFaceEngine =" + err.getCode());

		//ASAE_FSDKError err1 = mAgeEngine.ASAE_FSDK_UninitAgeEngine();
		//Log.d(TAG, "ASAE_FSDK_UninitAgeEngine =" + err1.getCode());

		//ASGE_FSDKError err2 = mGenderEngine.ASGE_FSDK_UninitGenderEngine();
		//Log.d(TAG, "ASGE_FSDK_UninitGenderEngine =" + err2.getCode());
	}

	@Override
	public Camera setupCamera() {
		// TODO Auto-generated method stub
		mCamera = Camera.open(mCameraID);
		try {
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(mWidth, mHeight);
			parameters.setPreviewFormat(mFormat);

			for( Camera.Size size : parameters.getSupportedPreviewSizes()) {
				Log.d(TAG, "SIZE:" + size.width + "x" + size.height);
			}
			for( Integer format : parameters.getSupportedPreviewFormats()) {
				Log.d(TAG, "FORMAT:" + format);
			}

			List<int[]> fps = parameters.getSupportedPreviewFpsRange();
			for(int[] count : fps) {
				Log.d(TAG, "T:");
				for (int data : count) {
					Log.d(TAG, "V=" + data);
				}
			}
			//parameters.setPreviewFpsRange(15000, 30000);
			//parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
			//parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
			//parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
			//parmeters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			//parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
			//parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
			mCamera.setParameters(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mCamera != null) {
			mWidth = mCamera.getParameters().getPreviewSize().width;
			mHeight = mCamera.getParameters().getPreviewSize().height;
		}
		return mCamera;
	}

	@Override
	public void setupChanged(int format, int width, int height) {

	}

	@Override
	public boolean startPreviewImmediately() {
		return true;
	}

	@Override
	public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {
		AFT_FSDKError err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result);
		Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.getCode());
		Log.d(TAG, "Face=" + result.size());


		/*for (AFT_FSDKFace face : result) {

			Log.d(TAG, "Face:" + face.toString());

		}*/
		if (mImageNV21 == null) {
			if (!result.isEmpty())
			{
				//mAFT_FSDKFace = result.get(0).clone();

                resultPerson.clear();
                resultPerson.addAll(result);


				mImageNV21 = data.clone();
			} else {
				if (!isPostted) {
					mHandler.removeCallbacks(hide);
					mHandler.postDelayed(hide, 2000);
					isPostted = true;
				}
			}
		}
		//copy rects
		Rect[] rects = new Rect[result.size()];
		for (int i = 0; i < result.size(); i++) {
			rects[i] = new Rect(result.get(i).getRect());
		}



		//clear result.
		result.clear();
		//return the rects for render.
		return rects;
	}

	@Override
	public void onBeforeRender(CameraFrameData data) {

	}

	@Override
	public void onAfterRender(CameraFrameData data) {
		mGLSurfaceView.getGLES2Render().draw_rect((Rect[])data.getParams(), Color.GREEN, 2);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		CameraHelper.touchFocus(mCamera, event, v, this);
		return false;
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		if (success) {
			Log.d(TAG, "Camera Focus SUCCESS!");
		}
	}

	//----------

    private String getPath(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(this, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                } else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(this, contentUri, null, null);
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(this, contentUri, selection, selectionArgs);
                }
            }
        }
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor actualimagecursor = this.getContentResolver().query(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        String end = img_path.substring(img_path.length() - 4);
        if (0 != end.compareToIgnoreCase(".jpg") && 0 != end.compareToIgnoreCase(".png")) {
            return null;
        }
        return img_path;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param mBitmap
     */
    /*private void startRegister(Bitmap mBitmap, String file) {
        Intent it = new Intent(DetecterActivity.this, RegisterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imagePath", file);
        it.putExtras(bundle);
        startActivityForResult(it, REQUEST_CODE_OP);
    }*/




}
