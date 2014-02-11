package com.lianzhuzhai.babywatcher;

import com.lianzhuzhai.babywatcher.stream.StreamIt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	SurfaceView sView;
	SurfaceHolder surfaceHolder;
	int screenWidth, screenHeight;
	Camera camera; // 定义系统所用的照相机
	boolean isPreview = false; // 是否在浏览中
	private String ipname;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);

		// 获取IP地址
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		ipname = data.getString("ipname");

		screenWidth = 640;
		screenHeight = 480;
		sView = (SurfaceView) findViewById(R.id.sView); // 获取界面中SurfaceView组件
		surfaceHolder = sView.getHolder(); // 获得SurfaceView的SurfaceHolder

		// 为surfaceHolder添加一个回调监听器
		surfaceHolder.addCallback(new Callback() {
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				initCamera(); // 打开摄像头
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// 如果camera不为null ,释放摄像头
				if (camera != null) {
					if (isPreview)
						camera.stopPreview();
					camera.release();
					camera = null;
				}
				System.exit(0);
			}
		});
		// 设置该SurfaceView自己不维护缓冲
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	private void initCamera() {
		if (!isPreview) {
			camera = Camera.open();
		}
		if (camera != null && !isPreview) {
			try {
				Camera.Parameters parameters = camera.getParameters();
				parameters.setPreviewSize(screenWidth, screenHeight); // 设置预览照片的大小
				parameters.setPreviewFpsRange(20, 30); // 每秒显示20~30帧
				parameters.setPictureFormat(ImageFormat.NV21); // 设置图片格式
				parameters.setPictureSize(screenWidth, screenHeight); // 设置照片的大小
				// camera.setParameters(parameters); // android2.3.3以后不需要此行代码
				camera.setPreviewDisplay(surfaceHolder); // 通过SurfaceView显示取景画面
				camera.setPreviewCallback(new StreamIt(ipname)); // 设置回调的类
				camera.startPreview(); // 开始预览
				camera.autoFocus(null); // 自动对焦
			} catch (Exception e) {
				e.printStackTrace();
			}
			isPreview = true;
		}
	}

}
