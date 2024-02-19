package com.jc.jcsports;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jc.jcsports.databinding.ActivityMainBinding;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("jcsports");
    }

    private ActivityMainBinding binding;
    private Handler handler;
    private Intent SttIntent;
    private SpeechRecognizer mRecognizer;
    private TextToSpeech tts;
    private RecognitionListener listener;


    //입력된 음성 메세지 확인 후 동작 처리
    private void FuncVoiceOrderCheck(@NonNull String VoiceMsg) {
        if (VoiceMsg.length() < 1) return;
        VoiceMsg = VoiceMsg.replace(" ", "");//공백제거
        FuncVoiceOut(VoiceMsg);
    }


    //음성 메세지 출력용
    private void FuncVoiceOut(@NonNull String OutMsg) {
        if (OutMsg.length() < 1) return;
        tts.setPitch(1.0f);
        tts.setSpeechRate(1.0f);
        tts.speak(OutMsg + "님을 확인중입니다", TextToSpeech.QUEUE_FLUSH, null);
        binding.webView.evaluateJavascript("findPerson(\"" + OutMsg + "\")", null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        if (mRecognizer != null) {
            mRecognizer.destroy();
            mRecognizer.cancel();
            mRecognizer = null;
        }
        handler.removeMessages(0);
        handler = null;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        hideSystemUI();
        webViewInit();
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    private void webViewInit() {
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                super.onPermissionRequest(request);
                request.grant(request.getResources());
            }
        });


        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                view.clearHistory();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }
        });
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        binding.webView.clearCache(true);
        binding.webView.clearHistory();
        binding.webView.addJavascriptInterface(new WebAppInterfaceImpl(), "Bridge");
        binding.webView.loadUrl("http://jcsport.cafe24.com:8080/");
        initSTT();
    }


    private class WebAppInterfaceImpl {
        @JavascriptInterface
        public void showToast(String msg) {
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void permissionVoice() {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

            } else {
                try {
                    handler.post(() -> {
                        mRecognizer.startListening(SttIntent);
                    });
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private void initSTT() {
        handler = new Handler();
        SttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        SttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        SttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);

        listener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

                Toast.makeText(MainActivity.this, "음성인식 시작", Toast.LENGTH_SHORT).show();
                Log.d("abc", "onReadyForSpeech...........");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("abc", "onBeginningOfSpeech...........");

            }

            @Override
            public void onRmsChanged(float v) {
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
                Log.d("abc", "onBufferReceived...........");
            }

            @Override
            public void onEndOfSpeech() {
                Log.d("abc", "onEndOfSpeech...........");
            }

            @Override
            public void onError(int i) {

                String message;
                switch (i) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "오디오 에러";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        //message = "클라이언트 에러";
                        //speechRecognizer.stopListening()을 호출하면 발생하는 에러
                        return; //토스트 메세지 출력 X
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "퍼미션 없음";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "네트워크 에러";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "네트웍 타임아웃";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        //message = "찾을 수 없음";
                        //녹음을 오래하거나 speechRecognizer.stopListening()을 호출하면 발생하는 에러
                        //speechRecognizer를 다시 생성하여 녹음 재개
                        return; //토스트 메세지 출력 X
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RECOGNIZER가 바쁨";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "서버가 이상함";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "말하는 시간초과";
                        break;
                    default:
                        message = "알 수 없는 오류임";
                        break;
                }

                Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                String key = SpeechRecognizer.RESULTS_RECOGNITION;
                List<String> mResult = results.getStringArrayList(key);
                String[] rs = new String[mResult.size()];
                mResult.toArray(rs);
                FuncVoiceOrderCheck(rs[0]);

            }

            @Override
            public void onPartialResults(Bundle bundle) {
                Log.d("abc", "onPartialResults");
            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        };
        mRecognizer.setRecognitionListener(listener);
        tts = new TextToSpeech(MainActivity.this, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.KOREAN);
            }
        });
    }

    public native String stringFromJNI();
}