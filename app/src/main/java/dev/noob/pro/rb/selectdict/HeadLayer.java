package dev.noob.pro.rb.selectdict;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by rb on 22/11/15.
 */
public class HeadLayer extends View
{
    String auth_key = "0dfa24e6-eacf-4f83-b52a-52d477a66a60";
    String text_key = "?key="+auth_key;
    String link_initial = "http://www.dictionaryapi.com/api/v1/references/collegiate/xml/";
    public String LOGGING = "LOGGING";
    String data;
    Context context;
    FrameLayout frameLayout;
    View view;
    WindowManager windowManager;
    TextView textview;
    ClipboardManager clipboardManager;
    ContentResolver cr;
    public HeadLayer(Context context)
    {
        super(context);
        this.context = context;
        this.frameLayout = new FrameLayout(context);
        addtoWindow();
    }
    private void addtoWindow()
    {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.END;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(frameLayout, params);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Here is the place where you can inject whatever layout you want.
        view = layoutInflater.inflate(R.layout.head, frameLayout);
        cr = context.getContentResolver();
        frameLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clicked();
                clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = clipboardManager.getPrimaryClip();
                data = (String)clipboardManager.getText();
                getMeaning(data);
                Log.d(LOGGING, "Inside Frame onclick");
            }
        });
        frameLayout.setOnLongClickListener(new OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                destroy();
                return true;
            }
        });
    }
    public void destroy()
    {
        windowManager.removeView(frameLayout);
    }
    public void clicked()
    {
        textview = (TextView)view.findViewById(R.id.text);
        textview.setVisibility(VISIBLE);
    }
    public void getMeaning(String data)
    {
        getfromapi task = new getfromapi();
        task.execute(data);
    }
    public class getfromapi extends AsyncTask<String,Void,Void>
    {
        Boolean internet_conn=true;
        String result;
        InputStream inputstream;
        @Override
        protected Void doInBackground(String... params)
        {
            String link_final=link_initial+params[0]+text_key;
            try
            {
                URL url = new URL(link_final);
                HttpURLConnection httpurlconn = (HttpURLConnection)url.openConnection();
                inputstream = httpurlconn.getInputStream();
                result=parseXML(inputstream);
            } catch (Exception e)
            {
                internet_conn=false;
                Log.d(LOGGING,"Exception : "+e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {

            if(!internet_conn)
            textview.setText("No Internet Connection");
            else
            {


                textview.setText(result);
                super.onPostExecute(aVoid);

            }
        }
    }
    public String parseXML(InputStream inputstream)
    {
        DocumentBuilderFactory documentfactory = DocumentBuilderFactory.newInstance();
        String value="";
        try
        {
            DocumentBuilder documentbuilder = documentfactory.newDocumentBuilder();
            Document document = documentbuilder.parse(inputstream);
            Element root = document.getDocumentElement();
            NodeList nodelist = root.getElementsByTagName("dt");
            Node node = nodelist.item(0);
            value = node.getTextContent();
        } catch (Exception e)
        {
            Log.d(LOGGING,"EXCEPTION : "+e);
        }
        return value;
    }
}
