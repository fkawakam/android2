package jp.kpiano.surface;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

public class MainActivity extends Activity implements PictureCallback {

	private SurfaceView sv;
	private SurfaceHolder sh;
	private Camera cm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sv = (SurfaceView) findViewById(R.id.surfaceView1);
		sh = sv.getHolder();
		sh.addCallback(new SampleCallback());
		sh = sv.getHolder();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class SampleCallback implements Callback {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			cm = Camera.open();
			try {
				cm.setPreviewDisplay(sv.getHolder());
			} catch (IOException e) {
				Log.d("FK No.100", e.toString(), e);
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			cm.stopPreview();
			Camera.Parameters parameters = cm.getParameters();
			boolean portrait = isPortrait();
			// 画面の向きを変更する
			if (portrait) {
				cm.setDisplayOrientation(90);
			} else {
				cm.setDisplayOrientation(0);
			}
			Log.d("FK No.200", String.format("orientation[%d]", orientation()));
			cm.setDisplayOrientation(90 * orientation());

			// サイズを設定
			List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
			Camera.Size size = sizes.get(0);
			parameters.setPreviewSize(size.width, size.height);

			// レイアウト調整
			Log.d("FK No.100", String.format("param (%d,%d) size(%d,%d)",
					width, height, size.width, size.height));
			// ViewGroup.LayoutParams layoutParams = sv.getLayoutParams();
			// if (portrait) {
			// layoutParams.width = size.height;
			// layoutParams.height = size.width;
			// } else {
			// layoutParams.width = size.width;
			// layoutParams.height = size.height;
			// }
			// sv.setLayoutParams(layoutParams);

			cm.setParameters(parameters);
			cm.startPreview();

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			cm.stopPreview();
			cm.release();
		}

	}

	protected boolean isPortrait() {
		return (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
	}

	private int orientation() {
		int rotation = getWindowManager().getDefaultDisplay().getRotation();
		if (rotation == 1) {
			rotation = 3;
		} else if (rotation == 3) {
			rotation = 1;
		}
		return rotation;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, null);
		MediaStore.Images.Media
				.insertImage(getContentResolver(), bmp, "", null);
	}

	public void onClickTakePicture(View view) {
		cm.takePicture(null, null, (PictureCallback) this);
	}

	public void onClickClose(View view) {
		finish();
	}

}