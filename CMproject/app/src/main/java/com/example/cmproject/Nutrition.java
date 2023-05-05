package com.example.cmproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Nutrition extends AppCompatActivity {

    private Button DownloadButton;
    ImageView image;
    TextView MealTextView;
    TextView MealTextView2;
    RelativeLayout parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        DownloadButton = (Button) findViewById(R.id.button7);
        MealTextView = (TextView) findViewById(R.id.textView3);
        MealTextView2 = (TextView) findViewById(R.id.textView4);
        image = (ImageView) findViewById(R.id.imageView3);

        parent = findViewById(R.id.parent_nutrition);

        AddRecipe();
        DownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ImageDownloader().execute("https://fitfoodiefinds.com/wp-content/uploads/2015/10/classic-oatmeal-196x196.jpg");
            }
        });

    }

    public void AddRecipe()
    {
        MealTextView.setText("Ingredients :\n" +
                             "- 4 cups water\n" +
                             "- 2 cups milk of choice (dairy or non-dairy)\n" +
                             "- 1 ½ cups steel cut oats\n" +
                             "- Scant ½ teaspoon kosher salt\n" +
                             "- ½ teaspoon cinnamon");
        MealTextView2.setText("Preparations :\n" +
                              "1.In a large pot, bring water and milk to a boil (watch closely to make sure that it does not boil over). Once boiling, add the steel cut oats, salt and cinnamon and stir to combine.\n" +
                              "2.Bring to a simmer over low heat and cook, stirring occasionally, for 20 to 25 minutes until the oats are creamy and tender. Serve immediately with desired toppings. Stores refrigerated up to 1 week: it will become very thick when chilled, so stir in some milk or water when reheating. ");
    }

    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        HttpURLConnection httpURLConnection;
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                Bitmap temp = BitmapFactory.decodeStream(inputStream);
                return temp;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpURLConnection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null){
                image.setImageBitmap(bitmap);
                Toast.makeText(getApplicationContext(), "Download Successful!", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(getApplicationContext(), "Download Error!", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}