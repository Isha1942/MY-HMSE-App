package com.spark.newapp2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AboutUsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_about_us_fragment, container, false);

        TextView tvAbout = view.findViewById(R.id.tv_about_us_content);

        String text = "We are ISO 9001:2015 certified Indian manufacturer of Security Alarm systems, established in 2018.\n\n" +
                "All products of our portfolio are CE and ROHS Certified.\n\n" +
                "We provide security solutions from small shops to big banks.\n\n" +
                "For more information please contact us:\n\n" +
                "🌐 Website: www.hmsetech.com\n" +
                "✉ Email: hmsetech@gmail.com, admin@hmsetech.com\n" +
                "📞 Mobile: +91 9834527412";

        SpannableString spannable = new SpannableString(text);

        // Website
        int websiteStart = text.indexOf("www.hmsetech.com");
        int websiteEnd = websiteStart + "www.hmsetech.com".length();
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.hmsetech.com"));
                startActivity(browserIntent);
            }
        }, websiteStart, websiteEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        int emailStart = text.indexOf("hmsetech@gmail.com");
        int emailEnd = emailStart + "hmsetech@gmail.com".length();
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                        Uri.parse("mailto:hmsetech@gmail.com"));
                startActivity(emailIntent);
            }
        }, emailStart, emailEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int email2Start = text.indexOf("admin@hmsetech.com");
        int email2End = email2Start + "admin@hmsetech.com".length();
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                        Uri.parse("mailto:admin@hmsetech.com"));
                startActivity(emailIntent);
            }
        }, email2Start, email2End, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        int mobileStart = text.indexOf("+91 9834527412");
        int mobileEnd = mobileStart + "+91 9834527412".length();
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:+919834527412"));
                startActivity(callIntent);
            }
        }, mobileStart, mobileEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvAbout.setText(spannable);
        tvAbout.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }
}
