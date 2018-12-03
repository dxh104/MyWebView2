package application.android.com.mywebview2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    WebView mWebView;
    Button button;

    @SuppressLint({"NewApi", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.wv);
        WebSettings webSettings = mWebView.getSettings();
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
//        1、网页地址是https地址，图片地址是http地址
//        2、Android 5.0.0
        webSettings.setBlockNetworkImage(false); // 解决图片不显示
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        // 设置允许JS弹窗
        mWebView.getSettings().setDomStorageEnabled(true);

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 先载入JS代码
        // 格式规定为:file:///android_asset/文件名.html
        mWebView.loadUrl("https://www.51dainiji.com/app/baoBiao?client=android&token=eea7c45395ad066bede1c452e7f31449");
        // 只需要将第一种方法的loadUrl()换成下面该方法即可
        button = findViewById(R.id.btn);
        mWebView.addJavascriptInterface(new AndroidtoJs(),"android");



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通过Handler发送消息
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        final int version = Build.VERSION.SDK_INT;
                        // 因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
                        if (version < 18) {
                            mWebView.loadUrl("javascript:callJS()");
                        } else {
                            mWebView.evaluateJavascript("javascript:callJS()", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    //此处为 js 返回的结果
                                }
                            });
                        }

                    }
                });

            }
        });
//        Android WebView的前进、后退、与刷新
//点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {  //表示按返回键 时的操作
                        mWebView.goBack();   //后退
                        mWebView.goForward();//前进
                        mWebView.reload();  //刷新
                        return true;    //已处理
                    }
                }
                return false;
            }
        });


        // 由于设置了弹窗检验调用结果,所以需要支持js对话框
        // webview只是载体，内容的渲染需要使用webviewChromClient类去实现
        // 通过设置WebChromeClient对象处理JavaScript的对话框
        //设置响应js 的Alert()函数
//        mWebView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
//                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
//                b.setTitle("Alert");
//                b.setMessage(message);
//                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        result.confirm();
//                    }
//                });
//                b.setCancelable(false);
//                b.create().show();
//                return true;
//            }
//        });


        //自定义
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                // return super.onJsAlert(view, url, message, result);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                Log.i("Tag----------url",url);
                Log.i("Tag----------message",message);
                result.confirm();//这里必须调用，否则页面会阻塞造成假死
                return true;

            }
        });



//        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                // 在开始加载网页时会回调
//                super.onPageStarted(view, url, favicon);
//            }
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                // 拦截 url 跳转,在里边添加点击链接跳转或者操作
//                view.loadUrl(url);
//                return true;
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                // 在结束加载网页时会回调
//
//                // 获取页面内容
//                view.loadUrl("javascript:window.java_obj.showSource("
//                        + "document.getElementsByTagName('html')[0].innerHTML);");
//
//                // 获取解析<meta name="share-description" content="获取到的值">
//                view.loadUrl("javascript:window.java_obj.showDescription("
//                        + "document.querySelector('meta[name=\"share-description\"]').getAttribute('content')"
//                        + ");");
//                super.onPageFinished(view, url);
//            }
//
//            @Override
//            public void onReceivedError(WebView view, int errorCode,
//                                        String description, String failingUrl) {
//                // 加载错误的时候会回调，在其中可做错误处理，比如再请求加载一次，或者提示404的错误页面
//                super.onReceivedError(view, errorCode, description, failingUrl);
//            }
//
//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view,
//                                                              WebResourceRequest request) {
//                // 在每一次请求资源时，都会通过这个函数来回调
//                return super.shouldInterceptRequest(view, request);
//            }
//
//        });
    }
    public class AndroidtoJs extends Object {
        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void gotoBillDetail(String msg) {
//            System.out.println("JS调用了Android的hello方法");
            Log.i("----------",msg);
        }
    }
}




