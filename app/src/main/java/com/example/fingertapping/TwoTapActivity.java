package com.example.fingertapping;

import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TwoTapActivity extends AppCompatActivity {

    private static final String TAG = "FingerTapping";
    private long firstTime;
    private float width;
    //private float y;
    private TextView info;
    private StringBuilder data = new StringBuilder(); //string builder przechowujacy dane, ktore mozna nastepnie zapisac do pliku
    private int counter = 0;

    private Map<String, Object> measuredData = new HashMap<>();
    private ArrayList<Long> times = new ArrayList<>();
    //    private ArrayList<Integer> valR = new ArrayList<>();
//    private ArrayList<Integer> valL = new ArrayList<>();
    private ArrayList<Integer> sideValue = new ArrayList<>(); // 0 - uniesienie paca, 10 - prawa strona, -10 - lewa strona
    private ArrayList<Float> distanceLeft = new ArrayList<>();

    private ArrayList<Float> distanceRight = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    float centreXR;
    float centreYR;
    float centreXL;
    float centreYL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_tap);

        info = (TextView) findViewById(R.id.twoTapInfo);
        ImageView leftAim = (ImageView) findViewById(R.id.leftAim);
        ImageView rightAim = (ImageView) findViewById(R.id.rightAim);
        ConstraintLayout layout = findViewById(R.id.layout);
        layout.setOnTouchListener(handleTouch);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;

        int[] i = new int[2];
        rightAim.getLocationInWindow(i);
        centreXR = i[0] + (rightAim.getWidth() / 2);
        centreYR = i[1] - (rightAim.getHeight() / 4);
        leftAim.getLocationInWindow(i);
        centreXL = i[0] + (leftAim.getWidth() / 2);
        centreYL = i[1] - (leftAim.getHeight() / 4);

    }

    private float calculateDist(float centreX, float centreY, float x, float y) {
        return (float) Math.sqrt(Math.pow((x - centreX), 2) + Math.pow((y - centreY), 2));
    }

    private View.OnTouchListener handleTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (counter == 1) {
                new CountDownTimer(10000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        System.out.println("heheszki" + counter);
                    }

                    public void onFinish() {
                        if (times.size() > 5) endOfMeasure();
                    }
                }.start();
            }

            //   if (counter < 2000) {
            long time;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float x = event.getX();
                    float y = event.getY();
                    //wziac czas dotkniecia i po ktorej stronie
                    Date date = new Date();
                    if (counter == 0) {
                        firstTime = date.getTime();
                        time = 0L;
                        System.out.println("aaa " + time);
                        times.add(time);
                        if (x > (width / 2)) { //prawa strona
                            float distance = calculateDist(centreXR, centreYR, x, y);
                            distanceRight.add(distance);
                            distanceLeft.add((float) -1);
                            String toData = time + ";" + 10 + ";" + distance + ";" + "-1" + "!";
                            data.append(toData);
                            sideValue.add(10);
                            // valL.add(0);
                            // valR.add(10);

                        } else { //lewa strona
                            float distance = calculateDist(centreXL, centreYL, x, y);
                            distanceLeft.add(distance);
                            distanceRight.add((float) -1);
                            String toData = time + ";" + "-10" + ";" + "-1" + ";" + distance + "!";
                            data.append(toData);
                            sideValue.add(-10);
                            // valL.add(10);
                            // valR.add(0);
                        }
                    } else {
                        time = date.getTime() - firstTime;
                        times.add(time);
                        if (x > (width / 2)) { //prawa strona
                            float distance = calculateDist(centreXR, centreYR, x, y);
                            distanceRight.add(distance);
                            distanceLeft.add((float) -1);
                            String toData = time + ";" + 10 + ";" + distance + ";" + "-1" + "!";
                            data.append(toData);
                            sideValue.add(10);
//                                valL.add(0);
//                                valR.add(10);

                        } else { //lewa strona
                            float distance = calculateDist(centreXL, centreYL, x, y);
                            distanceLeft.add(distance);
                            distanceRight.add((float) -1);
                            String toData = time + ";" + "-10" + ";" + "-1" + ";" + distance + "!";
                            data.append(toData);
                            sideValue.add(-10);
//                                valL.add(10);
//                                valR.add(0);
                        }
                    }

                    break;
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_UP:
                    //wziac czas puszczenia i po ktorej stronie
                    date = new Date();
                   // x = event.getX();
                   // y = event.getY();
                    distanceLeft.add((float) -1);
                    distanceRight.add((float) -1);
                    time = date.getTime() - firstTime;
                    times.add(time);
                   // if (x > (width / 2)) { //prawa strona
                        String toData = time + ";" + "0" + ";" + "-1" + ";" +"-1" + "!";
                        data.append(toData);
                        sideValue.add(0);
//                            valL.add(0);
//                            valR.add(20);
//                    } else { //lewa strona
//
//                        String toData = time + ";" + "0" + ";" + "-1" + ";" +"-1" + "!";
//                        data.append(toData);
//                        sideValue.add(0);
////                            valL.add(20);
////                            valR.add(0);
//                    }

                    counter++;
                    break;
            }
            // }
//            } else if (counter == 20) {
//               // endOfMeasure();
//                counter++;
//            }
            return true;

        }

    };


    private void endOfMeasure() {

        info.setText("Badanie zakończone!");
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd;HH:mm:ss");
        Date date = new Date();
        String fileName = "measurement " + dateFormat.format(date);
        new FileSave(this, fileName, data.toString());

        measuredData.put("time", times);
        measuredData.put("sideValue", sideValue);
//        test.put("valueR", valR);
//        test.put("valueL", valL);
        measuredData.put("distL", distanceLeft);
        measuredData.put("distR", distanceRight);

        db.collection(fileName)
                .add(measuredData)
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

//        db.collection("test")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });

        measuredData = new HashMap<>();
        times = new ArrayList<>();
        sideValue = new ArrayList<>();
//        valR = new ArrayList<>();
//        valL = new ArrayList<>();
        distanceLeft = new ArrayList<>();
        distanceRight = new ArrayList<>();
        data= new StringBuilder();
    }


}
