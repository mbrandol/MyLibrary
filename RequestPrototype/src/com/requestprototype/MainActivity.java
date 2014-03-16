package com.requestprototype;

import android.os.Bundle;

import android.view.Menu;
import com.requestprototype.R;
import java.util.ArrayList;
import java.util.HashMap;
 
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends ListActivity {

	private ProgressDialog pDialog;
	
	private String url = "https://www.googleapis.com/books/v1/volumes?q=inauthor:Zahn&key=AIzaSyB-6voXfmoB8qLjndx8m5R6aM12WpWUxcs";
	
    private static final String TAG_ITEMS = "items";
    private static final String TAG_SELFLINK = "selfLink";
    private static final String TAG_VOLUMEINFO = "volumeInfo";
    private static final String TAG_TITLE = "title";
    private static final String TAG_AUTHORS = "authors";
    
    JSONArray books = null;
    
    ArrayList<HashMap<String, String>> bookList;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        bookList = new ArrayList<HashMap<String, String>>();
        ListView lv = getListView();
        
        new GetBooks().execute();
    }
    
    private class GetBooks extends AsyncTask<Void, Void, Void> {
    	
    	protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();	
    	}
    	
    	protected Void doInBackground(Void... arg0) {
    		ServiceHandler sh = new ServiceHandler();
    		String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
    		Log.d("Response: ", "> " + jsonStr);
    		if (jsonStr != null) {
    			try {
    				JSONObject jsonObj = new JSONObject(jsonStr);
    				books = jsonObj.getJSONArray(TAG_ITEMS);
    				for (int i=0; i < books.length(); i++) {
    					JSONObject b = books.getJSONObject(i);
    					String selfLink = b.getString(TAG_SELFLINK);
    					JSONObject info = b.getJSONObject(TAG_VOLUMEINFO);
    					String title = info.getString(TAG_TITLE);
    					JSONArray bauthors = info.getJSONArray(TAG_AUTHORS);
    					String authors = bauthors.toString();
    					HashMap<String, String> book = new HashMap<String, String>();
    					book.put(TAG_TITLE, title);
    					book.put(TAG_AUTHORS, authors);
    					book.put(TAG_SELFLINK, selfLink);
    					bookList.add(book);
    				}
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    		}else {
    			Log.e("ServiceHandler", "Couldn't get any data from the url");
    		}
    		return null;
    	}
    	
    	protected void onPostExecute(Void result) {
    		super.onPostExecute(result);
    		if (pDialog.isShowing())
    			pDialog.dismiss();
    		ListAdapter adapter = new SimpleAdapter (
    				MainActivity.this, bookList,
    				R.layout.list_item, new String[] { TAG_TITLE, TAG_AUTHORS},
    				new int[] {R.id.title, R.id.authors});
    		setListAdapter(adapter);
    	}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
