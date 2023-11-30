package main;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Plotter {
    public static void plot(String plotName, String path, String xLabel, String yLabel, XYDataItem dimensions, XYDataItem[] points) {
        System.out.println("plotting...");

        XYSeries series = new XYSeries("");
        for (XYDataItem p : points)
            series.add(p);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(plotName, xLabel, yLabel, dataset, PlotOrientation.VERTICAL, true, true, false);

        try {
            ChartUtilities.saveChartAsPNG(new File(path), chart, dimensions.getX().intValue(), dimensions.getY().intValue());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void plot(String plotName, String path, String xLabel, String yLabel, XYDataItem dimensions, XYDataItem[][] points) {
        System.out.println("plotting...");
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (XYDataItem[] arr : points) {
            XYSeries series = new XYSeries("");
            for (XYDataItem p : arr)
                series.add(p);
            dataset.addSeries(series);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(plotName, xLabel, yLabel, dataset, PlotOrientation.VERTICAL, true, true, false);

        try {
            ChartUtilities.saveChartAsPNG(new File(path), chart, dimensions.getX().intValue(), dimensions.getY().intValue());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void plot(String plotName, String path, String xLabel, String yLabel, XYDataItem dimensions, ArrayList<XYDataItem> points) {
        XYDataItem[] pointsArray = new XYDataItem[points.size()];
        for (int i = 0; i < pointsArray.length; i++)
            pointsArray[i] = points.get(i);

        plot(plotName, path, xLabel, yLabel, dimensions, pointsArray);
    }
}