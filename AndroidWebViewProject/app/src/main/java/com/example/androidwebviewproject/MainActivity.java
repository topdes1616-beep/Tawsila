package com.example.androidwebviewproject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView myWebView;
    private String homeUrl = "https://tawsila-app-15ce7.web.app";
    private boolean isErrorPageLoaded = false;

    // HTML for the custom offline page
    private final String OFFLINE_HTML = "" +
            "<!DOCTYPE html>\n" +
            "<html lang=\"ar\" dir=\"rtl\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>لا يوجد اتصال بالإنترنت</title>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
            "            display: flex;\n" +
            "            flex-direction: column;\n" +
            "            justify-content: center;\n" +
            "            align-items: center;\n" +
            "            height: 100vh;\n" +
            "            margin: 0;\n" +
            "            background-color: #f0f2f5;\n" +
            "            color: #333;\n" +
            "            text-align: center;\n" +
            "        }\n" +
            "        .container {\n" +
            "            background-color: #fff;\n" +
            "            padding: 30px;\n" +
            "            border-radius: 10px;\n" +
            "            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);\n" +
            "            max-width: 90%;\n" +
            "        }\n" +
            "        h1 {\n" +
            "            color: #d32f2f;\n" +
            "            margin-bottom: 20px;\n" +
            "        }\n" +
            "        p {\n" +
            "            font-size: 1.1em;\n" +
            "            line-height: 1.6;\n" +
            "            margin-bottom: 30px;\n" +
            "        }\n" +
            "        button {\n" +
            "            background-color: #1976d2;\n" +
            "            color: white;\n" +
            "            border: none;\n" +
            "            padding: 12px 25px;\n" +
            "            border-radius: 5px;\n" +
            "            font-size: 1em;\n" +
            "            cursor: pointer;\n" +
            "            transition: background-color 0.3s ease;\n" +
            "        }\n" +
            "        button:hover {\n" +
            "            background-color: #1565c0;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <h1>عذراً!</h1>\n" +
            "        <p>لا يوجد اتصال بالإنترنت حالياً. يرجى التحقق من الشبكة والمحاولة مجدداً.</p>\n" +
            "        <button onclick=\"window.location.reload();\">إعادة المحاولة</button>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();

        // 1. JavaScript & Popups Support
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        // Optional: Enable zooming and other features
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        myWebView.setWebViewClient(new CustomWebViewClient());
        myWebView.setWebChromeClient(new CustomWebChromeClient());

        // Load the URL
        myWebView.loadUrl(homeUrl);
    }

    // 2. Custom Internet Error Handling
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (request.isForMainFrame()) {
                // Check for common network errors
                int errorCode = error.getErrorCode();
                if (errorCode == WebViewClient.ERROR_HOST_LOOKUP ||
                    errorCode == WebViewClient.ERROR_CONNECT ||
                    errorCode == WebViewClient.ERROR_TIMEOUT ||
                    errorCode == WebViewClient.ERROR_BAD_URL ||
                    errorCode == WebViewClient.ERROR_UNKNOWN) {
                    view.loadDataWithBaseURL(null, OFFLINE_HTML, "text/html", "utf-8", null);
                    isErrorPageLoaded = true;
                } else {
                    // For other errors, you might want to show a generic error or the default page
                    super.onReceivedError(view, request, error);
                }
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            isErrorPageLoaded = false; // Reset error flag when a new page starts loading
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // If the error page was loaded and then a new page finishes, it means we're back online
            if (isErrorPageLoaded && !url.equals("about:blank")) {
                isErrorPageLoaded = false;
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            // This ensures that all links within the WebView are opened in the WebView itself
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            new android.app.AlertDialog.Builder(view.getContext())
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> result.confirm())
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            new android.app.AlertDialog.Builder(view.getContext())
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> result.confirm())
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> result.cancel())
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsResult result) {
            final android.widget.EditText input = new android.widget.EditText(view.getContext());
            input.setText(defaultValue);
            new android.app.AlertDialog.Builder(view.getContext())
                    .setTitle(message)
                    .setView(input)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> result.confirm(input.getText().toString()))
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> result.cancel())
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            // You can log console messages here if needed for debugging
            // Log.d("WebViewConsole", consoleMessage.message() + " -- From line " +
            //         consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
            return super.onConsoleMessage(consoleMessage);
        }
    }

    // 3. Hardware Back Button Support
    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack() && !isErrorPageLoaded) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // Handle key events for back button if it's a physical button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack() && !isErrorPageLoaded) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
