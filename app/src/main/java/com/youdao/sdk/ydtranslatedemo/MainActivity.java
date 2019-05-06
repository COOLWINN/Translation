package com.youdao.sdk.ydtranslatedemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youdao.sdk.app.EncryptHelper;
import com.youdao.sdk.app.Language;
import com.youdao.sdk.app.LanguageUtils;
import com.youdao.sdk.common.Constants;
import com.youdao.sdk.ydonlinetranslate.SpeechTranslate;
import com.youdao.sdk.ydonlinetranslate.SpeechTranslateParameters;
import com.youdao.sdk.ydonlinetranslate.Translator;
import com.youdao.sdk.ydtranslate.Translate;
import com.youdao.sdk.ydtranslate.TranslateErrorCode;
import com.youdao.sdk.ydtranslate.TranslateListener;
import com.youdao.sdk.ydtranslate.TranslateParameters;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends Activity {

    public static final int UNUSED_REQUEST_CODE = 255;  // Acceptable range is [0, 255]

    LinearLayout strokeLayout;
    LinearLayout speakLayout;

    ProgressDialog progressDialog;
    EditText inputText;
    TextView resultText;
    Button translateBtn;
    ViewPager viewPager;
    MyPagerAdapter adapter;
    List<String> mData = new ArrayList<>();

    Translator translator;
    ExtAudioRecorder recorder;
    SpeechTranslateParameters tps;
    File audioFile;
    String filePath = null;
    String lastInput = null;
    CharSequence lastResult = null;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        applyPermissions();
    }

    //初始化控件
    public void initView() {

        //显示笔顺图的界面
        strokeLayout = findViewById(R.id.stroke_layout);
        //显示语音输入提示的界面
        speakLayout = findViewById(R.id.speak_layout);

        //显示正在加载中的提示框
        progressDialog = new ProgressDialog(MainActivity.this);
        //接受待翻译文本输入的可编辑框
        inputText = findViewById(R.id.input);
        //显示翻译结果的文本框
        resultText = findViewById(R.id.result_text);
        //翻译按钮
        translateBtn = findViewById(R.id.btn_translate);
        //展示多个汉字笔画顺序的滑动切换控件
        viewPager = findViewById(R.id.viewPager);
        //设置viewPager的预加载页面数量
        viewPager.setOffscreenPageLimit(3);
        //设置每个页面之间的距离
        viewPager.setPageMargin(60);
        //设置viewPager中每个页面之间的切换动画
        viewPager.setPageTransformer(true, new MyPagerTransformer());
        //初始化viewPager的数据适配器
        adapter = new MyPagerAdapter(mData, this);
        //将数据适配器绑定到viewpager上
        viewPager.setAdapter(adapter);
    }


    //录音监听器，主要实现了录音失败时应该如何显示
    ExtAudioRecorder.RecorderListener listener = new ExtAudioRecorder.RecorderListener() {
        @Override
        public void recordFailed(int failRecorder) {
            if (failRecorder == 0) {
                Toast.makeText(MainActivity.this, "Failure, please check if the recording permission is granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Unknown Error!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //开始录音
    public void startRecord(View view) {

        inputText.setText("");
        resultText.setText("");
        strokeLayout.setVisibility(View.GONE);
        speakLayout.setVisibility(View.VISIBLE);

        try {
            audioFile = File.createTempFile("record_", ".wav");
            AudioRecorderConfiguration configuration = new AudioRecorderConfiguration.Builder()
                    .recorderListener(listener)
                    .handler(handler)
                    .rate(Constants.RATE_16000)
                    .uncompressed(true)
                    .builder();
            recorder = new ExtAudioRecorder(configuration);
            recorder.setOutputFile(audioFile.getAbsolutePath());
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //结束录音
    public void stopRecord(View view) {

        if (recorder == null) {
            Toast.makeText(MainActivity.this, "请先录音", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mData.isEmpty()) {
            mData.clear();
            adapter.notifyDataSetChanged();
        }

        strokeLayout.setVisibility(View.VISIBLE);
        speakLayout.setVisibility(View.GONE);

        try {
            int time = recorder.stop();
            if (time > 0) {
                if (audioFile != null) {
                    filePath = audioFile.getAbsolutePath();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            recognize(filePath);
                        }
                    }).start();
                }
            }
            recorder.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //识别语音
    private void recognize(String filePath) {
        byte[] datas = null;
        try {
            datas = FileUtils.getContent(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String bases64 = EncryptHelper.getBase64(datas);

        //输入和输出音频格式都为wav格式
        tps = new SpeechTranslateParameters.Builder().source("youdaovoicetranslate")
                .from(Language.ENCH).to(Language.ENGLISH)
                .rate(Constants.RATE_16000)//输入音频码率，支持8000,16000
                .voice(Constants.VOICE_NEW)//输出声音，支持美式女生、美式男生、英式女生、英式男生
                .timeout(100000)//超时时间
                .build();

        SpeechTranslate.getInstance(tps).lookup(bases64, "requestId",
                new TranslateListener() {
                    @Override
                    public void onResult(final Translate result, String input, String requestId) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                String query = result.getQuery().toLowerCase();
                                inputText.setText(query.substring(0, query.length()-1));
                            }
                        });
                    }

                    @Override
                    public void onError(final TranslateErrorCode error, String requestId) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onResult(List<Translate> results, List<String> inputs, List<TranslateErrorCode> errors, String requestId) {

                    }
                });
    }

    // 翻译：英文—>中文
    public void translate(View view) {

        //获取待翻译的文本
        final String input = inputText.getText().toString();

        if (input.isEmpty()) {
            Toast.makeText(this, "Please input your text first !", Toast.LENGTH_LONG).show();
            return;
        }

        if (input.equals(lastInput)) {
            return;
        }

        //保存这一次的待翻译文本
        lastInput = input;
        //清空上一次查询的笔顺数据，刷新笔顺展示界面
        if (!mData.isEmpty()) {
            mData.clear();
            adapter.notifyDataSetChanged();
        }

        // 源语言或者目标语言其中之一必须为中文,目前只支持中文与其他几个语种的互译
        String from = "英文";
        String to = "中文";
        Language languageFrom = LanguageUtils.getLangByName(from);
        Language languageTo = LanguageUtils.getLangByName(to);
        TranslateParameters tps = new TranslateParameters.Builder()
                .source("youdao")
                .from(languageFrom)
                .to(languageTo)
                .sound(Constants.SOUND_OUTPUT_MP3)
                .voice(Constants.VOICE_BOY_UK)
                .timeout(3000)
                .build();
        translator = Translator.getInstance(tps);


        //查询待翻译文本的翻译结果
        translator.lookup(input, "requestId", new TranslateListener() {
            @Override
            public void onResult(final Translate result, String input, String requestId) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //在显示翻译结果时隐藏键盘
                        InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
                        resultText.setText(result.getTranslations().get(0));
                    }
                });
            }

            @Override
            public void onError(final TranslateErrorCode error, String requestId) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "查询错误:" + error.name(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResult(List<Translate> results, List<String> inputs, List<TranslateErrorCode> errors, String requestId) {

            }
        });
    }

    // 展示翻译结果中每一个汉字的笔画顺序
    public void displayStroke(View view) {

        //获取翻译后的文本
        CharSequence result = resultText.getText();
        //判断该文本是否为空或与上次查询结果是否一致，如果是，则不更新笔画顺序的展示数据；否则，进行更新
        if (result.length()==0 || result.equals(lastResult)) {
            Toast.makeText(this, "Please input or update your text to be translated first !", Toast.LENGTH_LONG).show();
        } else {
            lastResult = result;
            StrokeTask task = new StrokeTask();
            task.execute(result);
        }
    }

    //申请Android系统权限
    public void applyPermissions() {

        //如果targetSdkVersion设置为>=23的值，则需要申请权限
        if (noPermissionGranted(this, WRITE_EXTERNAL_STORAGE)) {
            String[] permissions = {WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, UNUSED_REQUEST_CODE);
        }
        if (noPermissionGranted(this, RECORD_AUDIO)) {
            String[] perssions = {RECORD_AUDIO};
            ActivityCompat.requestPermissions(this, perssions, UNUSED_REQUEST_CODE);
        }
    }

    //确认是否获得Android系统权限
    public static boolean noPermissionGranted(final Context context, final String permission) {
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri;
            if (data != null) {
                uri = data.getData();
                filePath = FileUtils.getPath(this, uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("StaticFieldLeak")
    private class StrokeTask extends AsyncTask<CharSequence, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //在得到笔顺数据之前弹出“数据正在加载中”的提示框
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(CharSequence... params) {
            try {
                //在子线程中进行网络访问，获取翻译结果的每一个汉字的笔顺数据(GIF图链接)，并保存到mData列表中
                for (int i = 0; i < params[0].length(); i++) {
                    String url = String.format("https://dict.baidu.com/s?wd=%s&ptype=zici", params[0].charAt(i));
                    Document doc = Jsoup.connect(url).get();
                    Elements element = doc.select("#word_bishun");
                    mData.add(element.attr("data-gif"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //通知viewpager的数据适配器adapter: 数据有更新，从而导致viewpager展示页面的更新
            adapter.notifyDataSetChanged();
            //关闭“数据正在加载中”的提示框
            progressDialog.dismiss();
        }
    }
}
