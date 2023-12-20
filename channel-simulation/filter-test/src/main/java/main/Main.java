package main;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;

public class Main {

    public Main() {
        XYSeries unfiltered = new XYSeries("unfiltered");
        XYSeries filtered = new XYSeries("filtered");

        double f1 = 1;
        double f2 = 10;
        Filter filter = new Filter(10, 0, 1000);

        for (double t = 0; t < 10; t += 0.0025) {
            double f = Math.sin(2 * Math.PI * f1 * t) + 0.1 * Math.sin(2 * Math.PI * f2 * t);
            unfiltered.add(t, f);
            f = filter.filter(f);
            filtered.add(t, f);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(unfiltered);
        dataset.addSeries(filtered);

        JFreeChart chart = ChartFactory.createXYLineChart("filter", "t", "A", dataset, PlotOrientation.VERTICAL, true, true, false);
        try {
            ChartUtilities.saveChartAsPNG(new File("../assets/filter.png"), chart, 1600, 900);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
