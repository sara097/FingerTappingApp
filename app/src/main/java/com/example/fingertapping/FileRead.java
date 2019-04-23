package com.example.fingertapping;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileRead {

    private Uri uri;
    private Context context;
    private StringBuilder stringBuilder;
    //serie danych odczytanych z pliku
    private XYSeries readSeriesX;
    private XYSeries readSeriesY;
    private XYSeries readSeriesZ;
    private XYMultipleSeriesRenderer mrenderer;

    public FileRead(Uri uri, Context context) {
        this.uri=uri;
        this.context=context;

    }

    public void readData() {


        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));
            stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);

            }

            reader.close();
            inputStream.close();


        } catch (java.io.IOException e) {
            //obsługa wyjątku wraz z wyswietleniem uzytkownikowi komunikatu
            Toast.makeText(context, "Cannot read data", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public void visualise(TextView info, LinearLayout chartLayout){

            info.setText(getFileName());
            drawTap(chartLayout);

    }

   private String getFileName() {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void drawPlot(LinearLayout chartLayout){

        readSeriesX = new XYSeries("data X");
        readSeriesY = new XYSeries("data Y");
        readSeriesZ = new XYSeries("data Z");

        //utworzenie rendererów serii danych i doprecyzowanie wyglądu serii danych na wykresie
        XYSeriesRenderer rendererX = new XYSeriesRenderer();
        rendererX.setLineWidth(2);
        rendererX.setColor(Color.MAGENTA);
        rendererX.setPointStyle(PointStyle.DIAMOND);
        rendererX.setPointStrokeWidth(6);
        rendererX.setLineWidth(3);

        XYSeriesRenderer rendererY = new XYSeriesRenderer();
        rendererY.setLineWidth(2);
        rendererY.setColor(Color.GREEN);
        rendererY.setPointStyle(PointStyle.DIAMOND);
        rendererY.setPointStrokeWidth(6);
        rendererY.setLineWidth(3);

        XYSeriesRenderer rendererZ = new XYSeriesRenderer();
        rendererZ.setLineWidth(2);
        rendererZ.setColor(Color.BLUE);
        rendererZ.setPointStyle(PointStyle.DIAMOND);
        rendererZ.setPointStrokeWidth(6);
        rendererZ.setLineWidth(3);

        //dodanie otworzonych wczesniej rendererów do listy rendererów i ustawienie maksimów i minimów wykresu
        mrenderer = new XYMultipleSeriesRenderer();
        mrenderer.addSeriesRenderer(rendererX);
        mrenderer.addSeriesRenderer(rendererY);
        mrenderer.addSeriesRenderer(rendererZ);
        mrenderer.setYAxisMax(15);
        mrenderer.setYAxisMin(-10);
        mrenderer.setShowGrid(true);

        String[] lines = stringBuilder.toString().split("!");
        String[] line = null;
        int counter;
        double aX;
        double aY;
        double aZ;

        for (String line1 : lines) {
            //wszystkie pomiary mialy po kilka wartosci rozdzielonych sredniakmi, wiec je tez rodzielamy
            line = line1.split(";");
            counter = Integer.valueOf(line[0]);
            aX = Double.valueOf(line[1]);
            aY = Double.valueOf(line[2]);
            aZ = Double.valueOf(line[3]);
            //dodajemy do serii danych wartosci
            readSeriesX.add(counter, aX);
            readSeriesY.add(counter, aY);
            readSeriesZ.add(counter, aZ);

        }

        //dodanie danych do wykresu
        XYMultipleSeriesDataset mdataset = new XYMultipleSeriesDataset();
        mdataset.addSeries(readSeriesX);
        mdataset.addSeries(readSeriesY);
        mdataset.addSeries(readSeriesZ);

        //wyswietlenie wykresu
        GraphicalView chartView = ChartFactory.getLineChartView(context, mdataset, mrenderer);
        chartLayout.addView(chartView);

    }

    private void drawTap(LinearLayout chartLayout){
        readSeriesX = new XYSeries("data right");
        readSeriesY = new XYSeries("data left");


        //utworzenie rendererów serii danych i doprecyzowanie wyglądu serii danych na wykresie
        XYSeriesRenderer rendererX = new XYSeriesRenderer();
        rendererX.setLineWidth(2);
        rendererX.setColor(Color.MAGENTA);
        rendererX.setPointStyle(PointStyle.DIAMOND);
        rendererX.setPointStrokeWidth(6);
        rendererX.setLineWidth(3);


        XYSeriesRenderer rendererZ = new XYSeriesRenderer();
        rendererZ.setLineWidth(2);
        rendererZ.setColor(Color.BLUE);
        rendererZ.setPointStyle(PointStyle.DIAMOND);
        rendererZ.setPointStrokeWidth(6);
        rendererZ.setLineWidth(3);

        //dodanie otworzonych wczesniej rendererów do listy rendererów i ustawienie maksimów i minimów wykresu
        mrenderer = new XYMultipleSeriesRenderer();
        mrenderer.addSeriesRenderer(rendererX);
        mrenderer.addSeriesRenderer(rendererZ);
        mrenderer.setYAxisMax(15);
        mrenderer.setYAxisMin(-10);
        mrenderer.setShowGrid(true);

        String[] lines = stringBuilder.toString().split("!");
        String[] line = null;
        double time;
        double right;
        double left;

        for (String line1 : lines) {
            //wszystkie pomiary mialy po kilka wartosci rozdzielonych sredniakmi, wiec je tez rodzielamy
            line = line1.split(";");
            time = Double.valueOf(line[0]);
            right = Double.valueOf(line[1]);
            left = Double.valueOf(line[2]);

            //dodajemy do serii danych wartosci
            readSeriesX.add(time, right);
            readSeriesY.add(time, left);

        }

        //dodanie danych do wykresu
        XYMultipleSeriesDataset mdataset = new XYMultipleSeriesDataset();
        mdataset.addSeries(readSeriesX);
        mdataset.addSeries(readSeriesY);

        //wyswietlenie wykresu
        GraphicalView chartView = ChartFactory.getLineChartView(context, mdataset, mrenderer);
        chartLayout.addView(chartView);
    }
}
