package io.github.ama_csail.amaexampleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.ama_csail.ama.AccessibleActivity;
import io.github.ama_csail.ama.menu.OnAccessibleMenuConnectedListener;
import io.github.ama_csail.ama.menu.OnInstructionsLoadedListener;
import io.github.ama_csail.amaexampleapp.news.Article;
import io.github.ama_csail.amaexampleapp.news.NewsApi;

public class MainActivity extends AccessibleActivity {

    private LinearLayout newsLayout;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsLayout = (LinearLayout) findViewById(R.id.news_layout);

        new NewsApiTask().execute(this);

        activity = this;

        String test = "this is a lint test";


    }

    protected void configureAccessibility() {

        //enableMenu();
        //enableDyslexiaFont(true);
        //AMA.increaseFontSize(getRootView(), 30);
        //AMA.setActionClass(newsLayout, ActionClass.DANGER);
        //Log.e("ACTION", AMA.getActionClass(newsLayout).toString());

        setOnAccessibleMenuConnectedListener(new OnAccessibleMenuConnectedListener() {
            @Override
            public void configureMenu() {

                // Create a glossary relevant to the information on the page
                provideGlossary(AccessibleDefinitions.getGlossary());

                // Create a process for loading instructions to be displayed to the user
                setOnInstructionsLoadedListener(null, new OnInstructionsLoadedListener() {
                    @Override
                    public void onInstructionsLoaded(ViewGroup parent, Object config) {
                        parent.removeAllViews();
                        LayoutInflater.from(activity)
                                .inflate(R.layout.main_instruction_layout, parent);
                    }
                });

                enableLanguageSettings();
            }
        });

    }

    /**
     * A collection of static variables which are used as configurations in the accessibility
     * process
     */
    public static class AccessibleDefinitions {

        public static Map<String, String> getGlossary() {
            Map<String, String> terms = new HashMap<>();
            terms.put("Accessible", "To be usable in different contexts, no matter the operator");
            terms.put("Glossary", "A list of terms and definitions");
            terms.put("Ars Technica", "A news source with articles about computer, technology, start-ups, and more.");
            terms.put("MIT", "An institute in Cambridge, MA which is renowned for it's incredible work in mobile accessibility, among other topics.");
            terms.put("AMA", "A project created to address the issues with mobile accessibility.");
            terms.put("Term", "This is just another term that I came up with");
            return terms;
        }

    }

    /**
     * Starts the process of downloading data from the New API
     */
    private class NewsApiTask extends AsyncTask<Context, Void, Void> {

        List<Article> headlines;
        Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Context... params) {

            context = params[0];
            headlines = NewsApi.getArsNews();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            newsLayout.removeAllViews();

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for (Article a : headlines) {

                View cardContainer = inflater.inflate(R.layout.news_headline, null);
                String byLine = "Written by " + a.getAuthor();
                ((TextView) cardContainer.findViewById(R.id.headline_title)).setText(a.getTitle());
                ((TextView) cardContainer.findViewById(R.id.headline_subline)).setText(byLine);

                Picasso.with(context)
                        .load(a.getUrlToImage())
                        .into((ImageView) cardContainer.findViewById(R.id.headline_image));

                final Article finalArticle = a;
                cardContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(finalArticle.getUrl());

                        // create an intent builder
                        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

                        intentBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
                        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

                        CustomTabsIntent customTabsIntent = intentBuilder.build();

                        customTabsIntent.launchUrl(activity, uri);

                    }
                });

                newsLayout.addView(cardContainer);
            }

            configureAccessibility();

        }

    }

}
