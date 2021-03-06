package com.wuwang.aavt.examples;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wuwang.aavt.av.Mp4Processor;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private PermissionAsker mAsker;

    private String tempPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.mp4";
    private android.widget.TextView progress;
    private android.widget.TextView time;
    Timer timerTask = new Timer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAsker = new PermissionAsker(10, new Runnable() {

            @Override
            public void run() {
                setContentView(R.layout.activity_main);
                progress = (TextView) findViewById(R.id.progress);
                time = (TextView) findViewById(R.id.time);
            }
        }, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "必要权限被拒绝，应用退出",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }).askPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mAsker.onRequestPermissionsResult(grantResults);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mMp4Process:
                startActivity(new Intent(this, ExampleMp4ProcessActivity.class));
                break;
            case R.id.mCameraRecord:
                startActivity(new Intent(this, CameraRecorderActivity.class));
                break;
            case R.id.mYuvExport:
                startActivity(new Intent(this, YuvExportActivity.class));
                break;
            case R.id.MP4progress_water:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                intent.setType("video/mp4"); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                //intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                break;
            case R.id.MP4progress_water2:
                Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                intent2.setType("video/mp4"); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                //intent.setType("*/*");//无类型限制
                intent2.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent2, 2);
                break;


            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            final String path = getRealFilePath(data.getData());
            if (path != null) {
                Log.d(TAG, "onActivityResult: path: " + path);
//                mMp4Processor.setInputPath(path);
                timerTask = new Timer();
                timerTask.schedule(new TimerTask() {
                    int index = 0;

                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ++index;
                                time.setText(index + " s");
                            }
                        });
                    }
                }, 0, 1000);


                try {
                    final long startTime = new Date().getTime();
                    if (requestCode == 1) { // 仅供测试，调试
                        VideoUtils.transcodeVideoFile(path, tempPath, 0, 0, MainActivity.this, new Mp4Processor.OnProgressListener() {
                            @Override
                            public void onProgress(final long max, final long current) {
                                Log.d(TAG, "onProgress: " + max + " " + current);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.setText("total: " + max / 1000000.0f + " progress: " + current / 1000000.0f);
                                    }
                                });
                            }

                            @Override
                            public void onComplete(final String path) {
                                Log.d(TAG, "onComplete: " + path);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timerTask.cancel();
                                        long endTime = new Date().getTime();
                                        progress.append(" \npath: " + path);
                                        progress.append(" \n耗时(s): " + (endTime - startTime) / 1000);
//                                    Intent v=new Intent(Intent.ACTION_VIEW);
//                                    v.setDataAndType(Uri.parse(path),"video/mp4");
//                                    MainActivity.this.startActivity(v);
                                    }
                                });
                            }
                        });
                    } else if (requestCode == 2) { // 项目中已经有使用
                        String[] title = new String[2];
                        String[] desc = new String[3];
                        title[0] = "徐实打实大苏打实打实实打实大苏打是寅尹";
                        title[1] = "徐实打实大苏打实打实实打实大苏打是寅尹";
                        desc[0] = "徐实打实大苏打实打实实打实大苏打是寅尹";
                        desc[1] = "徐实打实大苏打实打实实打实大苏打是寅尹";
                        desc[2] = "徐实打实大苏打实打实实打实大苏打是寅尹";
                        com.example.mp4processor.utils.VideoUtils.start(path, tempPath,
                                R.mipmap.logo,
                                R.mipmap.ic_launcher,
                                R.mipmap.icon_place_stroke,
                                title,
                                desc,
                                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                                "ttttttttttttttt",
                                "location",
                                MainActivity.this,
                                new com.example.mp4processor.av.Mp4Processor.OnProgressListener() {
                                    @Override
                                    public void onProgress(final long max, final long current) {
                                        Log.d(TAG, "onProgress: " + max + " " + current);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progress.setText("total: " + max / 1000000.0f + " progress: " + current / 1000000.0f);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onComplete(final String outpath) {
                                        Log.d(TAG, "onComplete: " + outpath);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                timerTask.cancel();
                                                long endTime = new Date().getTime();
                                                progress.append(" \npath: " + path);
                                                progress.append(" \noutpath: " + outpath);
                                                File f = new File(path);
                                                progress.append(" \ninputSize: " + f.length() / (1024 * 1024.0));
                                                File f1 = new File(outpath);
                                                progress.append(" \noutputSize: " + f1.length() / (1024 * 1024.0));
                                                progress.append(" \n耗时(s): " + (endTime - startTime) / 1000);
//                                    Intent v=new Intent(Intent.ACTION_VIEW);
//                                    v.setDataAndType(Uri.parse(path),"video/mp4");
//                                    MainActivity.this.startActivity(v);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError() {
                                        Log.d(TAG, "onError: ");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progress.setText("error");
                                            }
                                        });
                                    }
                                });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getRealFilePath(final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            Log.e("wuwang", "scheme is null");
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
            Log.e("wuwang", "SCHEME_FILE");
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            data = GetPathFromUri4kitkat.getPath(getApplicationContext(), uri);
        }
        return data;
    }
}
