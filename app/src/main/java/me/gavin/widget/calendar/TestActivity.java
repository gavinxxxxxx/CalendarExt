package me.gavin.widget.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.just.agentweb.AgentWeb;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2018/4/4
 */
public class TestActivity extends AppCompatActivity {

    private AgentWeb mA;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mA = AgentWeb.with(this)
                .setAgentWebParent((ViewGroup) findViewById(R.id.holder), new FrameLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        mA.getJsAccessEntrace().quickCallJs("initChatHeight", "500");
                    }
                })
                .createAgentWeb()
                .ready()
                .go("http://test2.ymm.cn:9011/IMSystem/LiveChat/AppChat?onlyCode=77a12ada4ead441191bb3ce72eeac8bb&liveId=ca4a0f8e6d12407ba04efc3619eb49d4");

        final EditText editText = findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mA.getJsAccessEntrace().quickCallJs("initChatHeight", v.getText().toString());
                }
                return true;
            }
        });
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.setHint(String.valueOf(findViewById(R.id.holder).getHeight()));
            }
        });
    }
}
