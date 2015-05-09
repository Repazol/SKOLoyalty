package com.repa.skoloyalty;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

class Question {
    String id="-1";
    String tmpl;
    String quest="No text";
    String[] answer;
    String[] answerimg;
    String bgcolor;
    String btncolor;
    String qfont;
    String afont;
    String q_offs;
    String a_offs;
    String condition;
    int d_width=0;
    int d_height=0;
    String bgimage;
    String bgbtnimage;
}
class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final String DEBUG_TAG = "Gestures in Example Class";

    @Override
    public boolean onDoubleTap(MotionEvent event) {

        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        // if there is a double tap, show the action bar

        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {

        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());

        return false;
    }

    @Override
    public boolean onDown(MotionEvent event) {

        Log.d(DEBUG_TAG, "onDown: " + event.toString());

        return true;

    }

} // end-of-Example Class
class FontRec
{
    String font="Arial";
    int size=14;
    int color= 255*256*256*256;
    int shcolor= 0;
    int shoffs= 0;

}
class ImgCachRec
{
    String u;
    String f;
}
public class MainActivity extends Activity {

    boolean Debug=false;
    TextView LogText;
    TextView QuestionText;
    DisplayMetrics metrics;

    String password;
    String place;
    String filial;
    String us;
    String ps;
    String showsett;
    SharedPreferences sPref;
    String basedir;
    String bgdir="backgrounds";
    ArrayList ImgCach = new ArrayList();

    private GestureDetectorCompat mDetector;


    private static final String TAG = "SKOLog";

    String id_place="-1";
    //String url="http://279e6bf8.ngrok.com/sko";
    String url="http://sqloyalty.com";
    //String url="http://virt1/sko";


    String LastAnswer;
    Question[] Questions = new Question[100];
    int QuestionsCount=0;
    int QuestionNumber=0;

    //LinearLayout MainLayout;
    LinearLayout BackGround;
    LinearLayout ButtonsContent;
    //LinearLayout QuestContent;
    Timer timer;
    TimerTask tTask;
    long interval = 10000;

    View.OnClickListener BtnClick;

    private static final int AUTO_HIDE_DELAY_MILLIS = 10;

    View contentView;


    private Handler handler = new Handler();
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            //contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            //getWindow().getDecorView().setSystemUiVisibility(View.GONE);
            HideAll();
            //contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            handler.postDelayed(this, AUTO_HIDE_DELAY_MILLIS);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(task,  AUTO_HIDE_DELAY_MILLIS);
    }

    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(task);
    }

    void saveText() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("pass", password);
        ed.putString("filial", filial);
        ed.putString("place", place);
        ed.putString("us", us);
        ed.putString("ps", ps);
        ed.putString("showsett", showsett);

        ed.commit();
        Toast.makeText(this, "Пароль сохранен", Toast.LENGTH_SHORT).show();
    }

    void loadText() {
        sPref = getPreferences(MODE_PRIVATE);
        password = sPref.getString("pass", "");
        filial = sPref.getString("filial","");
        place = sPref.getString("place","");
        us = sPref.getString("us","");
        ps = sPref.getString("ps","");
        showsett = sPref.getString("showsett","N");
        if (showsett.equals("Y"))
        {
            TextView v = (TextView) findViewById(R.id.SettingsButton);
            v.setText("        ");

        }
    }
    private void createDirectoryIfNeeded(String directoryName)
    {
        File theDir = new File(directoryName);

        if (!theDir.exists())
        {
            theDir.mkdir();
        }
    }
    FontRec ExpandFont (String s)
    {
        FontRec f = new FontRec();
        String[] d= s.split("\\|");
        f.font=d[0];
        f.size=Integer.decode(d[1]);
        f.color=Integer.parseInt(d[2].substring(1), 16)+255*256*256*256;
        try {
           f.shcolor=Integer.parseInt(d[3].substring(1), 16)+255*256*256*256;
           f.shoffs=Integer.decode(d[4]);

        } catch (Exception e) {
        e.printStackTrace();
    }
        return f;
    }

    public void CreateButtons(Question quest) {
        ButtonsContent.removeAllViews();
        ButtonsContent.setPadding(0, 2*Integer.decode(quest.a_offs.split("\\|")[1]), 0, 0);
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT);
        int btnGravity = Gravity.CENTER;
        lParams.gravity = btnGravity;
        //lParams.weight=1;
        if (quest.btncolor.equals("")) {quest.btncolor="#00000000";}
        int bColor = Integer.parseInt(quest.btncolor.substring(1), 16)+255*256*256*256;
        Log.d(TAG, "Create Buttons");
        FontRec fq=ExpandFont(quest.afont);
        int wdth=Integer.decode(quest.a_offs.split("\\|")[0]);
        int mxw=metrics.widthPixels / (quest.answer.length+1);
        int mxh=-1;
        for (int i=0;i<quest.answer.length;i++) {
            if (quest.answer[i]!="") {
                Button btnNew = new Button(this);
                btnNew.setText(quest.answer[i]);
                btnNew.setShadowLayer(fq.shoffs*2,fq.shoffs,fq.shoffs,fq.shcolor);
                btnNew.getBackground().setColorFilter(bColor, PorterDuff.Mode.MULTIPLY);
                String bg=quest.bgbtnimage;
                try {
                    if (!quest.answerimg[i].equals("")) {
                        bg=quest.answerimg[i];
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mxh=lParams.WRAP_CONTENT;

                if (!bg.equals(""))
                {
                    try {
                        Log.d(TAG, " try bg fn:" + bg);
                        FileInputStream fs = new FileInputStream(bg);
                        Drawable d = Drawable.createFromStream(fs, null);
                        btnNew.setBackground(d);
                        fs.close();
                        Bitmap tgtImg = BitmapFactory.decodeFile(bg);
                        if (tgtImg!=null) {
                            float k;
                            k=(float) (mxw)/(float) (tgtImg.getWidth());
                            mxh= (int) (tgtImg.getHeight()*k);
                            //Log.d(TAG, "Img WxH (k):"+tgtImg.getWidth()+'x'+tgtImg.getHeight()+" ("+k+")");
                            //Log.d(TAG, "Button WxH:"+mxw+"x"+mxh);

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "Background image error:"+e.getMessage());
                    }
                }

                btnNew.setPadding(10, 10, 10, 10);
                btnNew.setTextColor(fq.color);
                btnNew.setTextSize(fq.size);
                if (quest.tmpl.equals("1")) {
                    //btnNew.setMinWidth(Integer.decode(quest.q_offs.split("\\|")[0]));
                    lParams.width=lParams.WRAP_CONTENT;
                    lParams.height=mxh;
                    btnNew.setMaxWidth(mxw);
                    lParams.width=mxw;
                }
                if (quest.tmpl.equals("2")) {

                    lParams.width=lParams.MATCH_PARENT;
                    lParams.height=lParams.WRAP_CONTENT;
                }

                btnNew.setTag(i);
                btnNew.setOnClickListener(BtnClick);
                //TextView fil1 = new TextView(this);
                //fil1.setWidth(wdth);
                //ButtonsContent.addView(fil1, lParams);
                ButtonsContent.addView(btnNew, lParams);
                //TextView fil = new TextView(this);
                //fil.setWidth(wdth);

                //ButtonsContent.addView(fil, lParams);
            }
        }
    }

    public void PrepareQuestion (Question quest) {
        ViewGroup.LayoutParams lp = BackGround.getLayoutParams();
        ViewGroup.LayoutParams bt = ButtonsContent.getLayoutParams();
        ViewGroup.LayoutParams qp = QuestionText.getLayoutParams();

        FontRec fq=ExpandFont(quest.qfont);
        QuestionText.setText(quest.quest);
        QuestionText.setPadding(0, 2*Integer.decode(quest.q_offs.split("\\|")[1]), 0, 0);
        QuestionText.setTextColor(fq.color);
        QuestionText.setTextSize(fq.size);
        QuestionText.setShadowLayer(2,fq.shoffs,fq.shoffs,fq.shcolor);
        if (quest.bgcolor.equals("")) {quest.bgcolor="#00000000";}
        int bColor = Integer.parseInt(quest.bgcolor.substring(1), 16)+255*256*256*256;
        BackGround.setBackgroundColor(bColor);
        if (!quest.bgimage.equals(""))
        {
            try {
                Log.d(TAG, " try bg fn:" + quest.bgimage);
                FileInputStream fs = new FileInputStream(quest.bgimage);
                //Drawable d = Drawable.createFromStream(getAssets().open(quest.bgimage), null);
                Drawable d = Drawable.createFromStream(fs, null);
                BackGround.setBackground(d);
                fs.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Background image error:"+e.getMessage());
            }
        }
        Log.d(TAG, "Template:"+quest.tmpl+" "+quest.quest);
        if (quest.tmpl.equals("1")) {
            BackGround.setOrientation(BackGround.VERTICAL);
            ButtonsContent.setOrientation(ButtonsContent.HORIZONTAL);
            qp.width=qp.MATCH_PARENT;
            qp.height=qp.WRAP_CONTENT;
            bt.width=bt.MATCH_PARENT;
            bt.height=lp.WRAP_CONTENT;
        }
        if (quest.tmpl.equals("2")) {
            BackGround.setOrientation(BackGround.HORIZONTAL);
            ButtonsContent.setOrientation(ButtonsContent.VERTICAL);
            qp.width=quest.d_width/3;
            qp.height=lp.WRAP_CONTENT;
            bt.width=bt.MATCH_PARENT;
            bt.height=bt.MATCH_PARENT;
        }

        CreateButtons(quest);

    }

    void ShowQuestion (int n) {

        Log.d(TAG, "Show question #"+n);
        if (n==0) {LastAnswer="";}
        if (n<QuestionsCount)
        {
            PrepareQuestion(Questions[n]);
        }
        Log.d(TAG, "Show question Done");
        schedule();
    }

    void ReloadQuestions()
    {
        QuestionText.setText("Загрузка...");
        String req = Uri.parse(url + "/getinfo")
                .buildUpon()
                .appendQueryParameter("q", password)
                .appendQueryParameter("f", filial)
                .appendQueryParameter("p", place)
                .appendQueryParameter("us", us)
                .appendQueryParameter("ps", ps)
                .build().toString();
        new MyParser().execute(req);
        Log.d(TAG, "Request:"+req);
    }
    void ClearFrame() {
        QuestionText.setText("Загрузка");
        ButtonsContent.removeAllViews();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event){

        this.mDetector.onTouchEvent(event);
        Log.d(TAG, "onTouchEvent: " + event.toString());

        return super.onTouchEvent(event);
    }
    public void HideAll() {
        contentView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        mDetector = new GestureDetectorCompat(this, new GestureListener());

        basedir=getApplicationInfo().dataDir;
        createDirectoryIfNeeded(basedir+"/"+bgdir);
        timer = new Timer();

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        LogText = (TextView) findViewById(R.id.LogText);
        LogText.setText("Running...");
        BackGround = (LinearLayout) findViewById(R.id.quest_content);
        //MainLayout = (LinearLayout) findViewById(R.id.MainLyaout);
        ButtonsContent = (LinearLayout) findViewById(R.id.ButtonConent);
        QuestionText = (TextView) findViewById(R.id.QuestionText);

        BtnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAnswer(Questions[QuestionNumber].id,v.getTag().toString());
                //doAnswer(v.getTag().toString());
            }
        };
        if (!Debug) {LogText.setHeight(0);}
        Button bs = (Button) findViewById(R.id.SettingsButton);
        bs.setOnClickListener(mCorkyListener);

        contentView = findViewById(R.id.MainLyaout);
        HideAll();

        //contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        ClearFrame();
        loadText();
        ReloadQuestions();
        Log.d(TAG, "onCreate Done..");

    }
    private View.OnClickListener mCorkyListener = new View.OnClickListener() {
        public void onClick(View v) {
            onSettingsButtonClick (v);
        }
    };
    public void onSettingsButtonClick (View v) {
        ShowSettingsForm();
        Log.d(TAG, "Settings click...");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        password = data.getStringExtra("pass");
        filial = data.getStringExtra("filial");
        place = data.getStringExtra("place");
        us = data.getStringExtra("us");
        ps = data.getStringExtra("ps");
        showsett=data.getStringExtra("showsett");
        saveText();
        ReloadQuestions();
    }

    public void ShowSettingsForm()
    {
        Intent intent = new Intent(this, com.repa.skoloyalty.Settings.class);
        intent.putExtra("password", password);
        intent.putExtra("place", place);
        intent.putExtra("filial", filial);
        intent.putExtra("us", us);
        intent.putExtra("ps", ps);
        intent.putExtra("showsett", showsett);

        startActivityForResult(intent,1);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            ShowSettingsForm();
            return true;
        }

        if (id == R.id.action_reload) {
            ReloadQuestions();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    boolean CondTrue (String cnd)
    {
        String[] aa=cnd.split("\\|");
        boolean r = cnd.equals("*");
        for (int i = 0; i < aa.length; i++) {
            if (aa[i].equals(LastAnswer)) {r=true;}
        }
        Log.d(TAG, "Answer '"+LastAnswer+"' on condition:'"+cnd+"' is "+r);
        Log.d(TAG, "Condition count:"+aa.length);
        return r;
    }
    void PostQuestion (String id_q, String answ){
        String req = Uri.parse(url+"/putinfo")
                .buildUpon()
                .appendQueryParameter("q", password)
                .appendQueryParameter("p", id_place)
                .appendQueryParameter("us", us)
                .appendQueryParameter("ps", ps)
                .appendQueryParameter("idq", id_q)
                .appendQueryParameter("a", answ)
                .build().toString();
        Log.d(TAG, req);
        new MyPoster().execute(req);
    }
    public void doAnswer (String id_q, String ans) {
        Log.d(TAG, "Answer #"+ans+" on question id:"+id_q);
        int id_a=Integer.decode(ans);
        LastAnswer=Questions[QuestionNumber].answer[id_a];
        PostQuestion (id_q, LastAnswer);
        if (QuestionNumber+1<QuestionsCount)
        {
            QuestionNumber++;
            while (!CondTrue(Questions[QuestionNumber].condition))
            {
                if (QuestionNumber+1<QuestionsCount) {
                    QuestionNumber++;
                }    else {
                    QuestionNumber=0;
                    break;
                }
            }
        } else {QuestionNumber=0;}
        ShowQuestion(QuestionNumber);
    }
    final Handler uiHandler = new Handler();
    // Parsers ------------------------------------------------------------------------------------------------------
    private class MyParser extends AsyncTask<String, Void, String> {
        String err;

        public String clear(String s) {
            String result;
            result =s.replace("\\","");
            return result;
        }
        String GetDiv (Document doc, String s) {
            String r;
            Element qs = doc.select(s).first();
            if (qs!=null) {
                r = qs.text();
            } else {
                r="";
            }
            return r;
        }
        void downloadFromUrl(URL url, String localFilename) throws IOException {
            InputStream is = null;
            FileOutputStream fos = null;

            try {
                URLConnection urlConn = url.openConnection();//connect

                is = urlConn.getInputStream();               //get connection inputstream
                fos = new FileOutputStream(localFilename);   //open outputstream to local file

                byte[] buffer = new byte[4096];              //declare 4KB buffer
                int len;

                //while we have availble data, continue downloading and storing to local file
                while ((len = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                Log.d(TAG, "Download done");
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                }
            }
        }
        int GetCashIndex (String ur) {
            int r = -1;
            for (int i = 0; i < ImgCach.size(); i++) {
                ImgCachRec IC = (ImgCachRec) ImgCach.get(i);
                if (IC.u.equals(ur)) {
                    r = i;
                    break;
                }
            }
            return r;
        }
        void ShowCash () {
            for (int i = 0; i < ImgCach.size(); i++) {
                ImgCachRec IC = (ImgCachRec) ImgCach.get(i);
                Log.d(TAG, IC.u +"->"+IC.f);
            }
        }
        String GetImage(String ur,String fn) {
            String r="";
            ImgCachRec dt;
            int k=GetCashIndex(ur);
            if (k!=-1)
            {
                dt= (ImgCachRec) ImgCach.get(k);
                r=dt.f;
                Log.d(TAG, "GetImage from cash:" + r);

            }
            else {
                try {//basedir+"/"+bgdir+"/bg
                    String extension = ur.substring(ur.lastIndexOf("."));
                    String u;
                    u = url + ur;
                    Log.d(TAG, "Try GetImage:" + u);
                    URL website = new URL(u);
                    String fn1 = basedir + "/" + bgdir + "/" + fn + extension;
                    Log.d(TAG, "    fn:" + fn1);
                    downloadFromUrl(website, fn1);
                    r = fn1;
                    dt = new ImgCachRec();
                    dt.u=ur;
                    dt.f=r;
                    ImgCach.add(dt);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "GetImage error:" + e.getMessage());
                }
            }
            Log.d(TAG, "GetImage:" + r);
            return r;
        }
        @Override
        protected String doInBackground(String... links) {
            String rez = "OK";
            String QuestName;
            String QuestChange;
            Log.d(TAG, "Start Init");
            Document doc = null;
            ImgCach.clear();
            try {
                doc = Jsoup.connect(links[0]).get();

                /*Element err = doc.select("div#error").first();
                if (err!=null) {
                    rez = err.text();
                } */
                rez=GetDiv(doc,"div#error");
                err=rez;
                Log.d(TAG, "Error div:"+rez);


                if (rez.equals("")) {

                    QuestName = GetDiv(doc, "div#quest-name");
                    QuestChange = GetDiv(doc, "div#quest-change");
                    id_place = GetDiv(doc, "div#id-place");

                    int n = 0;

                    String st = "div#question-";
                    String answers = "div#question-";
                    Element qs = doc.select(st + n + "-id").first();
                    while (qs != null) {
                        Question q = new Question();
                        q.id = qs.text();
                        q.tmpl = GetDiv(doc, st + n + "-tmpl");
                        q.quest = GetDiv(doc, st + n + "-question");
                        answers = GetDiv(doc, st + n + "-answers");
                        q.bgcolor = GetDiv(doc, st + n + "-bgcolor");
                        qs = doc.select(st + n + "-btncolor").first();
                        q.btncolor = qs.text();
                        qs = doc.select(st + n + "-qfont").first();
                        q.qfont = qs.text();
                        qs = doc.select(st + n + "-afont").first();
                        q.afont = qs.text();
                        qs = doc.select(st + n + "-q_offs").first();
                        q.q_offs = qs.text();
                        qs = doc.select(st + n + "-a_offs").first();
                        q.a_offs = qs.text();
                        qs = doc.select(st + n + "-condition").first();
                        q.condition = qs.text();

                        q.answer = answers.split("\\|");

                        q.d_height = metrics.heightPixels;
                        q.d_width = metrics.widthPixels;

                        q.bgimage=GetDiv(doc,st + n + "-bgimage");
                        if (!q.bgimage.equals(""))
                        {
                            q.bgimage=GetImage(q.bgimage,"bg"+n);
                        }
                        q.bgbtnimage=GetDiv(doc,st + n + "-bgbimage");
                        if (!q.bgbtnimage.equals(""))
                        {
                            q.bgbtnimage=GetImage(q.bgbtnimage,"bgb"+n);
                        }
                        //answerimg btnimages
                        String abcde;
                        abcde=GetDiv(doc, st + n + "-btnimages");
                        q.answerimg = abcde.split("\\|");
                        for (int i=0;i<q.answerimg.length;i++)
                        {
                            if (!q.answerimg[i].equals("")) {
                                Log.d(TAG, "BUTTONS IMAGE:"+q.answerimg[i]);
                                q.answerimg[i]=GetImage(q.answerimg[i],"bgbg"+n+"-"+i);
                            }
                        }

                        Questions[n] = q;

                        n++;
                        qs = doc.select(st + n + "-id").first();

                    }
                    QuestionsCount = n;
                    rez = "OK";
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Init error:"+e.getMessage());
                rez=rez+" >>"+e.getLocalizedMessage();
            }
            Log.d(TAG, "End Init");
            for (int i=0;i<10;i++)
            {
                if (Questions[i]!=null) {Log.d(TAG, ">>"+Questions[i].quest);}
            }
            ShowCash ();
            return rez;

        }

        @Override
        protected void onPostExecute(String result) {
            QuestionText.setText("                      ");
            if (result != "OK") {
                //LogText.setText("Ошибка:"+result);
                QuestionText.setText("Ошибка:"+err);
                //onSettingsButtonClick(null);
            } else {
                //LogText.setText("Ok");
                QuestionNumber=0;
                ShowQuestion (QuestionNumber);
            }
        }
    }

    void schedule() {
        if (tTask != null) tTask.cancel();
        if (interval > 0) {
            tTask = new TimerTask() {
                public void run() {
                    if (QuestionNumber!=0) {
                        QuestionNumber = 0;
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ShowQuestion(QuestionNumber);
                            }
                        });
                    }
                }
            };
            timer.schedule(tTask, interval, interval);
        }
    }


    private class MyPoster extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... links) {
            Log.d(TAG, "Posting data");
            String rez="OK";
            Document doc = null;
            try {
                doc = Jsoup.connect(links[0]).get();

            } catch (Exception e) {
                e.printStackTrace();
                rez=e.getMessage();
                Log.d(TAG, "Post error:"+rez);
            }
            return rez;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != "OK") {
                LogText.setText("Ошибка:"+result);
            } else {
                LogText.setText(result);
            }
        }
    }


}
