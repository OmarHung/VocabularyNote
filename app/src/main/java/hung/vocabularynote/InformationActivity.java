package hung.vocabularynote;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class InformationActivity extends AppCompatActivity {
    TextView txtInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        txtInfo = (TextView) findViewById(R.id.txt_info);
        String url = "　　本程式可供使用者隨時隨地記錄下新學的單字，當手上無紙筆時可作為臨時筆記本，也可隨時拿起手機並開啟程式複習所學的單字，歡迎各路高手批評與指教，使我能更精進此程式。";
        //String url = "　　本程式屬開源軟體，原始碼在 <a href=\"https://github.com/OmarHung/VocabularyNote\">Github</> 中，歡迎各路高手批評指教。";
        txtInfo.setMovementMethod(LinkMovementMethod.getInstance());
        txtInfo.setText(getClickableHtml(url));

    }
    private void setLinkClickable(final SpannableStringBuilder clickableHtmlBuilder, final URLSpan urlSpan) {
        int start = clickableHtmlBuilder.getSpanStart(urlSpan);
        int end = clickableHtmlBuilder.getSpanEnd(urlSpan);
        int flags = clickableHtmlBuilder.getSpanFlags(urlSpan);
        ClickableSpan clickableSpan = new ClickableSpan() {
            public void onClick(View view) {
                Log.i("InformationActivity", "onClick url=" + urlSpan.getURL() );
                //Log.d("InformationActivity", "onClick");
                //Do something with URL here.
                //Uri uri = Uri.parse("http://www.google.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlSpan.getURL()));
                startActivity(intent);
            }
        };
        clickableHtmlBuilder.setSpan(clickableSpan, start, end, flags);
    }

    private CharSequence getClickableHtml(String html) {
        Spanned spannedHtml = Html.fromHtml(html);
        SpannableStringBuilder clickableHtmlBuilder = new SpannableStringBuilder(spannedHtml);
        URLSpan[] urls = clickableHtmlBuilder.getSpans(0, spannedHtml.length(), URLSpan.class);
        for(final URLSpan span : urls) {
            setLinkClickable(clickableHtmlBuilder, span);
        }
        return clickableHtmlBuilder;
    }
}
