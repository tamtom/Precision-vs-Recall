package com.itdeve.precisionvsrecall;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private int c;
    private HashMap<Integer,float[]> theIndices ;
    private HashMap<Integer,float[][]> table;
   private Button draw;
    private Button nextDoc;
    private EditText numOfSets;
    private EditText indeces;
    private TextView forq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        c =1;
    nextDoc = (Button) findViewById(R.id.next);
        draw   = (Button) findViewById(R.id.draw);
        numOfSets  = (EditText) findViewById(R.id.num_of_sets);
        indeces = (EditText) findViewById(R.id.indices_of_relevant_docs);
        forq = (TextView) findViewById(R.id.for_q1);
        theIndices =  new HashMap<>();
        table = new HashMap<>();


draw.setEnabled(false);

        nextDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {
                    String indexes = indeces.getText().toString();
                    StringTokenizer tokenizer = new StringTokenizer(indexes, ",");
                    float[] index = new float[tokenizer.countTokens()];
                    int i = 0;
                    while (tokenizer.hasMoreTokens()) {
                        index[i] = Integer.parseInt(tokenizer.nextToken().trim());
                        i++;
                    }
                    theIndices.put(c, index);
                    indeces.setText("");
                    calculateRecallAndPrecision();

                    c++;
                    if (c > Integer.parseInt(numOfSets.getText().toString())) {
                        draw.setEnabled(true);
                        nextDoc.setEnabled(false);
                    }
                    forq.setText("for Q" + c);


                } else
                    Toast.makeText(getApplicationContext(), "somthing went wrong", Toast.LENGTH_SHORT).show();
            }
        });
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LineChartActivity.class);
                i.putExtra("map",table);
                startActivity(i);
            }
        });

    }

    private void calculateRecallAndPrecision() {
        float tablear [][] = new float[11][2];
        DecimalFormat df2 = new DecimalFormat("0.##");

        for (int i = 0; i <theIndices.get(c).length; i++) {

            String num =df2.format(((float)(i+1)/theIndices.get(c).length));
           Log.d("the number born with",num);
            if(num.length()>=4){
                int r = Character.getNumericValue(num.charAt(3));
                if(r>=5)
                {
                    float numint = Float.parseFloat(num);
                    float def = 10 -r;
                   def/=100;

                    num =Double.toString(Math.round((numint+def)* 100.0) / 100.0);


                }
                else {
                    float numint = Float.parseFloat(num);
                    float def = r;
                    def/=100;
                    num =Float.toString( numint-def );

                }

            }


            tablear[(int)(Float.parseFloat(num)*10)][0] = Float.parseFloat(num)*100;
            Log.d("the values before inter in index "+((int)(Float.parseFloat(num)*10)),df2.format(((float)( (i+1)/theIndices.get(c)[i]))*100));
            tablear[(int)(Float.parseFloat(num)*10)][1] =Float.parseFloat(df2.format(((float)( (i+1)/theIndices.get(c)[i]))*100));

        }
        interpolation(tablear);
        table.put(c, tablear);

    }

    private void interpolation(float[][] tablear) {

        for (int i = 9; i>=0 ; i--) {
               if(tablear[i][0]==0.0f){

                   tablear[i][0] = (float)(i*10);

                   tablear[i][1] = tablear[i+1][1];
               }

        }
        if(tablear[10][0]==0){
            tablear[10][0] = (float)(10*10);
            tablear[10][1] = 0;
        }
    }

    private boolean check() {
        if(numOfSets.getText().toString().isEmpty())
            return  false;
        if (Integer.parseInt(numOfSets.getText().toString())<c){
            nextDoc.setEnabled(false);
            return false;
 }
        if(indeces.getText().toString().isEmpty())
            return  false;


        return true;


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
