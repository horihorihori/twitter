package com.example.hori.twitter;

import android.accounts.Account;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

import java.util.List;
import java.util.zip.Inflater;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
//import twitter4j.TwitterStream;
//import twitter4j.TwitterStreamFactory;
import twitter4j.User;
//import twitter4j.UserStreamAdapter;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public class MyTwitter extends ListActivity {


    private TweetAdapter mAdapter;
    private Twitter mTwitter;

    private static final int CONTEXT_MENU_ID_1 = 0;
    private static final int CONTEXT_MENU_ID_2 = 1;
    private static final int CONTEXT_MENU_ID_3 = 2;

//    private MyUserStreamAdapter mMyUserStreamAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //トークンのありなし判定
        if (!TwitterUtils.hasAccessToken(this)) {
        } else {


            //ListActivityにおける通常のListViewでいうsetAdapterメソッド
            mAdapter = new TweetAdapter(this);
            setListAdapter(mAdapter);

            mTwitter = TwitterUtils.getTwitterInstance(this);
            loadTimeLine();

            registerForContextMenu(getListView());

        }

    }

//コンテキストメニュー未実装
/*
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.add(0, CONTEXT_MENU_ID_1, 0, "返信");
        menu.add(0, CONTEXT_MENU_ID_2, 0, "RT");
        menu.add(0, CONTEXT_MENU_ID_3, 0, "お気に入り");

    }

    //コンテキストメニューの操作
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //特定のuserに向けたリプライ機能
            case CONTEXT_MENU_ID_1:

                return true;

            //以下ArrayListに格納してポジションを取得する方法？
            //Adapterからポジションを取得？
            case CONTEXT_MENU_ID_2:

                return true;
            case CONTEXT_MENU_ID_3:

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            //更新
            loadTimeLine();
            return true;
        } else if (id == R.id.action_account) {
            //アカウント追加ボタン
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_tweet){
            //ツイート画面へ
            Intent intent = new Intent(this, TweetActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private class TweetAdapter extends ArrayAdapter<twitter4j.Status> {

        //LayoutInflaterはこのActivity以外からも参照できるようになる
        private LayoutInflater mInflater;

        public TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        //getView()でXMLで設定したビュー
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = mInflater.inflate(R.layout.activity_my, null);
            }
            Status item = getItem(position);
            //名前
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(item.getUser().getName());
            //ID
            TextView screenName = (TextView) view.findViewById(R.id.screen_name);
            screenName.setText("@" + item.getUser().getScreenName() + "/");
            TextView text = (TextView) view.findViewById(R.id.text);
            text.setText(item.getText());
            /*
            *アイコン
            *別のライブラリ、AndroidSmartImageView
            */
            SmartImageView icon = (SmartImageView) view.findViewById(R.id.icon);
            icon.setImageUrl(item.getUser().getProfileImageURL());
            return view;
        }

    }


    //更新
    private void loadTimeLine() {
        AsyncTask<Void, Void, List<twitter4j.Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return mTwitter.getHomeTimeline();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    mAdapter.clear();
                    for (twitter4j.Status status : result) {
                        mAdapter.add(status);
                    }
                    getListView().setSelection(0);
                } else {
                    //取得できなかった時
                }
            }
        };
        task.execute();
    }


    //Toast
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }





}