package com.example.fingertapping;

import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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
import java.util.Timer;
import java.util.TimerTask;

public class TwoTapActivity extends AppCompatActivity {

    private long firstTime;
    private float width;
    private float y;
    private TextView info;
    private StringBuilder data = new StringBuilder(); //string builder przechowujacy dane, ktore mozna nastepnie zapisac do pliku
    private int counter = 0;
    private ImageView leftAim;
    private ImageView rightAim;

    private Map<String, Object> test = new HashMap<>();
    private ArrayList<Long> times = new ArrayList<>();
    private ArrayList<Integer> valR = new ArrayList<>();
    private ArrayList<Integer> valL = new ArrayList<>();
    private ArrayList<Float> left = new ArrayList<>();
    private ArrayList<Float> right = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_tap);

        info = (TextView) findViewById(R.id.twoTapInfo);
        leftAim = (ImageView) findViewById(R.id.leftAim);
        rightAim = (ImageView) findViewById(R.id.rightAim);
        ConstraintLayout layout = findViewById(R.id.layout);
        layout.setOnTouchListener(handleTouch);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;



    }

    private float calculateDist(float centreX, float centreY, float x, float y){
        return (float) Math.sqrt(Math.pow((x-centreX),2)+ Math.pow((y-centreY),2));
    }

    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if(counter==1){
                new CountDownTimer(10000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        System.out.println("heheszki" + counter);
                    }

                    public void onFinish() {
                        if(valL.size()>5)
                            endOfMeasure();

                    }

                }.start();
            }


            int[] i = new int[2];
            rightAim.getLocationInWindow(i);
            float centreXR = i[0] + rightAim.getWidth() / 2;
            float centreYR = i[1] - rightAim.getHeight() / 4;
            leftAim.getLocationInWindow(i);
            float centreXL = i[0] + leftAim.getWidth() / 2;
            float centreYL = i[1] - leftAim.getHeight() / 4;

            if (counter < 2000) {
                long time;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        float x = event.getX();
                        System.out.println("lol " + y + ", " + centreYR + " , " + centreYL);
                        y = event.getY();
                        //wziac czas dotkniecia i po ktorej stronie
                        Date date = new Date();
                        if (counter == 0) {
                            firstTime = date.getTime();
                            time = 0L;
                            System.out.println("aaa " + time);
                            times.add(time);
                            if (x > (width / 2)) { //prawa strona
                                right.add(calculateDist(centreXR, centreYR, x, y));
                                left.add((float) -1);

                                String toData = time + ";" + 1 + ";" + 0 + "!";
                                data.append(toData);
                                valL.add(0);
                                valR.add(10);

                            } else { //lewa strona
                                left.add(calculateDist(centreXL, centreYL, x, y));
                                right.add((float) -1);
                                String toData = time + ";" + 0 + ";" + 1 + "!";
                                data.append(toData);
                                valL.add(10);
                                valR.add(0);
                            }
                        } else {
                            time = date.getTime() - firstTime;
                            times.add(time);
                            System.out.println("aaa " + time);
                            if (x > (width / 2)) { //prawa strona
                                right.add(calculateDist(centreXR, centreYR, x, y));
                                left.add((float) -1);
                                System.out.println("xdd " + calculateDist(centreXR, centreYR, x, y));
                                String toData = time + ";" + 10 + ";" + 0 + "!";
                                data.append(toData);

                                valL.add(0);
                                valR.add(10);

                            } else { //lewa strona
                                left.add(calculateDist(centreXL, centreYL, x, y));
                                right.add((float) -1);
                                System.out.println("xdd " + calculateDist(centreXL, centreYL, x, y));
                                String toData = time + ";" + 0 + ";" + 10 + "!";
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
                        y = event.getY();
                        left.add((float) -1);
                        right.add((float) -1);
                        time = date.getTime() - firstTime;
                        System.out.println("aaa " + time);
                        times.add(time);
                        if (x > (width / 2)) { //prawa strona

                            String toData = time + ";" + 10 + ";" + 0 + "!";
                            data.append(toData);

                            valL.add(0);
                            valR.add(20);
                        } else { //lewa strona

                            String toData = time + ";" + 0 + ";" + 10 + "!";
                            data.append(toData);

                            valL.add(20);
                            valR.add(0);
                        }

                        counter++;
                        break;
                }
            }
//            } else if (counter == 20) {
//               // endOfMeasure();
//                counter++;
//            }
            return true;

        }

    };



    private void endOfMeasure() {

        info.setText("Badanie zakończone!");
       // DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd;HH:mm:ss");
       // Date date = new Date();
      //  String fileName = "twoTap;" + dateFormat.format(date);
       // FileSave fileSave = new FileSave(this, fileName, data.toString());

        final String TAG = TwoTapActivity.class.getSimpleName();
        test.put("time", times);
        test.put("valueR", valR);
        test.put("valueL", valL);
        test.put("distL", left);
        test.put("distR", right);
        System.out.println("ehe " + Arrays.asList(test)); // method 1
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

       test = new HashMap<>();
        times = new ArrayList<>();
        valR = new ArrayList<>();
        valL = new ArrayList<>();
        left = new ArrayList<>();
        right = new ArrayList<>();
    }


}
