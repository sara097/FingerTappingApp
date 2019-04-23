package com.example.fingertapping;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TwoTapActivity extends AppCompatActivity {

    private Date date;
    private long firstTime;
    private long time;
    private ArrayList<Long> milsLeft = new ArrayList<>();
    private ArrayList<Long> milsRight = new ArrayList<>();
    private float width;
    private float x;
    private TextView info;
    private ConstraintLayout layout;
    private StringBuilder data = new StringBuilder(); //string builder przechowujacy dane, ktore mozna nastepnie zapisac do pliku
    private int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_tap);

        info = (TextView) findViewById(R.id.twoTapInfo);
        layout = findViewById(R.id.layout);
        layout.setOnTouchListener(handleTouch);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;



    }


    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (counter < 20) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = event.getX();
                        //wziac czas dotkniecia i po ktorej stronie
                        date = new Date();
                        if(counter==0) {
                            firstTime = date.getTime();
                            time = firstTime - firstTime;
                            System.out.println("aaa "+time);
                            times.add(time);
                            if (x > (width / 2)) { //prawa strona
                                String toData = time + ";" + 1 +";"+0+"!";
                                data.append(toData);
                                valL.add(0);
                                valR.add(10);

                            } else { //lewa strona

                                String toData = time + ";" + 0 +";"+1+"!";
                                data.append(toData);
                                valL.add(10);
                                valR.add(0);
                            }
                        }
                        else{
                            time = date.getTime() - firstTime;
                            times.add(time);
                            System.out.println("aaa "+time);
                            if (x > (width / 2)) { //prawa strona
                                String toData = time + ";" + 10 +";"+0+"!";
                                data.append(toData);

                                valL.add(0);
                                valR.add(10);

                            } else { //lewa strona

                                String toData = time + ";" + 0 +";"+10+"!";
                                data.append(toData);

                                valL.add(10);
                                valR.add(0);
                            }
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        //wziac czas puszczenia i po ktorej stronie
                        date = new Date();
                        x = event.getX();
                        time = date.getTime() - firstTime;
                        System.out.println("aaa "+time);
                        times.add(time);
                        if (x > (width / 2)) { //prawa strona
                            String toData = time + ";" + 10 +";"+0+"!";
                            data.append(toData);

                            valL.add(0);
                            valR.add(10);
                        } else { //lewa strona

                            String toData = time + ";" + 0 +";"+10+"!";
                            data.append(toData);

                            valL.add(10);
                            valR.add(0);
                        }

                        counter++;
                        break;
                }
            } else if (counter==20){
                endOfMeasure();
                counter++;
            }
                return true;

            }

    };

    Map<String, Object> test= new HashMap<>();
    ArrayList<Long> times=new ArrayList<>();
    ArrayList<Integer> valR=new ArrayList<>();
    ArrayList<Integer> valL=new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private void endOfMeasure(){

            info.setText("Badanie zakończone!");
            DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd;HH:mm:ss");
            Date date = new Date();
            String fileName = "twoTap;" + dateFormat.format(date);
            FileSave fileSave = new FileSave(this, fileName, data.toString());

        final String TAG= TwoTapActivity.class.getSimpleName();
        test.put("time", times);
        test.put("valueR", valR);
        test.put("valueL", valL);
        System.out.println("ehe"+ Arrays.asList(test)); // method 1
        db.collection("test")
                .add(test)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        db.collection("test")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }



}
