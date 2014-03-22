package com.bookprototype;

import android.os.Bundle;
import android.os.AsyncTask;
import android.app.Activity;
import android.view.*;
import android.widget.EditText;
import android.widget.Toast;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    class HttpGetter extends AsyncTask<String, Void, String> {
    	protected String doInBackground(String... url) {
      	  InputStream content = null;
      	  try {
      	    HttpClient httpclient = new DefaultHttpClient();
      	    HttpResponse response = httpclient.execute(new HttpGet(url[0]));
      	    content = response.getEntity().getContent();
      	  } catch (Exception e) {
      		  e.printStackTrace();
      	  }
          String s = "";
          String line = "";
          
          // Wrap a BufferedReader around the InputStream
          BufferedReader rd = new BufferedReader(new InputStreamReader(content));
          
          // Read response until the end
          try {
          	while ((line = rd.readLine()) != null) { s += line; }
          } catch (Exception e) {
          	Toast toast = Toast.makeText(getApplicationContext(), "Error loading string", Toast.LENGTH_SHORT);
      		toast.show();
          }
          
          return s;
    	}
    	
    	protected void onPostExecute(String s) {
    		super.onPostExecute(s);
    	}
    }
    
    public static InputStream getInputStreamFromUrl(String url) {
    	  InputStream content = null;
    	  try {
    	    HttpClient httpclient = new DefaultHttpClient();
    	    HttpResponse response = httpclient.execute(new HttpGet(url));
    	    content = response.getEntity().getContent();
    	  } catch (Exception e) {
    		  e.printStackTrace();
    	  }
    	    return content;
    	}
    
    public void buttonClick (View view) {
    	EditText isbnText = (EditText)findViewById(R.id.isbnText);
    	String isbnNum = isbnText.getText().toString();
    	String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbnNum;
    	//InputStream searchResult = getInputStreamFromUrl(url);
    	//String searchString = inputStreamToString(searchResult);
    	String searchString = "";
    	try {
    		searchString = new HttpGetter().execute(url, null, searchString).get();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    		String TAG_ITEMS = "items";
    		String TAG_VOLUMEINFO = "volumeInfo";
    		String TAG_TITLE = "title";
    		String TAG_AUTHORS = "authors";
    	try {
    		JSONObject returnedList = new JSONObject(searchString);
        	try {
        		JSONArray bookList = returnedList.getJSONArray(TAG_ITEMS);
            	try {
            		JSONObject book = bookList.getJSONObject(0);
                	try {
                		JSONObject bookInfo = book.getJSONObject(TAG_VOLUMEINFO);
                		String bookTitle = bookInfo.getString(TAG_TITLE);
                		JSONArray authors = bookInfo.getJSONArray(TAG_AUTHORS);
                		String bookAuthor = authors.getString(0);
                		EditText titleText = (EditText)findViewById(R.id.titleText);
                		titleText.setText(bookTitle);
                		EditText authorText = (EditText)findViewById(R.id.authorText);
                		authorText.setText(bookAuthor);
                	} catch (JSONException e) {
                		e.printStackTrace();
                	}
            	} catch (JSONException e) {
                	Toast toast = Toast.makeText(getApplicationContext(), "Get the book", Toast.LENGTH_LONG);
                	toast.show();
            	}
        	} catch (JSONException e) {
            	Toast toast = Toast.makeText(getApplicationContext(), "Get the Array", Toast.LENGTH_LONG);
            	toast.show();
        	}
    	} catch (JSONException e) {
        	Toast toast = Toast.makeText(getApplicationContext(), "Read the Object", Toast.LENGTH_LONG);
        	toast.show();
    	}

    }
}