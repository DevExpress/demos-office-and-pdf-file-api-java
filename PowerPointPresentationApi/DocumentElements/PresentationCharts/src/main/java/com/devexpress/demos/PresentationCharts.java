package com.devexpress.demos;

import com.devexpress.docs.office.AxisPositionType;
import com.devexpress.docs.office.BarSeries;
import com.devexpress.docs.office.ChartAxisGroupType;
import com.devexpress.docs.office.ChartLineOptions;
import com.devexpress.docs.office.ChartNumericData;
import com.devexpress.docs.office.ChartStringData;
import com.devexpress.docs.office.ChartStyle;
import com.devexpress.docs.office.ChartTitle;
import com.devexpress.docs.office.ChartTitleOptions;
import com.devexpress.docs.office.LegendPositionType;
import com.devexpress.docs.office.LineSeries;
import com.devexpress.docs.office.LineStyle;
import com.devexpress.docs.office.OutlineStyle;
import com.devexpress.docs.office.SolidFill;
import com.devexpress.docs.office.TextArea;
import com.devexpress.docs.office.TrendLine;
import com.devexpress.docs.office.TrendLineType;
import com.devexpress.docs.presentation.Chart;
import com.devexpress.docs.presentation.DocumentFormat;
import com.devexpress.docs.presentation.Presentation;
import com.devexpress.docs.presentation.Slide;
import com.devexpress.docs.presentation.SlideLayoutType;
import com.devexpress.office.utils.Units;
import com.devexpress.system.drawing.Color;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class PresentationCharts {

    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PresentationCharts.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "Presentation_Charts";
    static final boolean IS_PDF_OUTPUT = true;
    static final String CHART_TITLE = "Example Chart";
    static final float CHART_HEIGHT = 5f;
    static final boolean SHOW_TRENDLINE = true;
    static final boolean SHOW_DATA_LABELS = true;
    static final boolean SHOW_AXIS_TITLES = true;
    static final boolean SHOW_GRID_LINES = true;
    static final boolean SHOW_LEGEND = true;
    static final LegendPositionType LEGEND_POSITION = LegendPositionType.RIGHT;
    static final ChartStyle CHART_STYLE = ChartStyle.COLOR;

    public static void main(String[] args) {
        try {
            ByteArrayOutputStream outputStream = createPresentationChart();

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME, OUTPUT_DIRECTORY_DEMO_NAME);
            outputDirectory.mkdirs();
            String outputFormat = IS_PDF_OUTPUT ? "pdf" : "pptx";
            File outputFile = new File(outputDirectory, OUTPUT_FILE_NAME + "." + outputFormat);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                outputStream.writeTo(fos);
            }
            System.out.println("Created " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    static ByteArrayOutputStream createPresentationChart() throws IOException {
        try (Presentation presentation = new Presentation()) {
            presentation.getSlides().clear();
            Slide slide = new Slide(SlideLayoutType.BLANK);
            presentation.getSlides().add(slide);

            float chartHeightInDocuments = Units.inchesToDocumentsF(CHART_HEIGHT);
            Chart chart = new Chart(100, 100, chartHeightInDocuments * 2, chartHeightInDocuments);
            slide.getShapes().add(chart);

            TextArea textArea = new TextArea(CHART_TITLE);
            textArea.getParagraphs().get(0).getProperties().getTextProperties()
                .setFill(new SolidFill(Color.fromArgb(255, 0x1D, 0x2B, 0x64)));
            chart.setTitle(new ChartTitle(textArea));

            if (SHOW_LEGEND)
                chart.getLegend().setPosition(LEGEND_POSITION);
            else
                chart.setLegend(null);

            List<String> months = Arrays.asList("January", "February", "March", "April", "May", "June");
            List<Double> units = Arrays.asList(50.0, 100.0, 30.0, 104.0, 87.0, 150.0);
            List<Double> transactions = Arrays.asList(900.0, 3000.0, 1200.0, 7000.0, 5100.0, 7500.0);

            BarSeries unitsSoldSeries = new BarSeries();
            unitsSoldSeries.setText("Units Sold");
            unitsSoldSeries.setArguments(new ChartStringData(months));
            unitsSoldSeries.setValues(new ChartNumericData(units));

            if (SHOW_TRENDLINE) {
                LineStyle trendLineStyle = new LineStyle();
                trendLineStyle.setWidth(2);
                trendLineStyle.setFill(new SolidFill(Color.getRed()));

                TrendLine trendLine = new TrendLine();
                trendLine.setName(unitsSoldSeries.getText() + " trend");
                trendLine.setType(TrendLineType.LINEAR);
                trendLine.setOutlineStyle(trendLineStyle);
                unitsSoldSeries.setTrendLine(trendLine);
            }

            unitsSoldSeries.getDataLabels().setShowValue(SHOW_DATA_LABELS);
            chart.getSeries().add(unitsSoldSeries);

            LineSeries totalTransactionsSeries = new LineSeries();
            totalTransactionsSeries.setText("Total Transactions");
            totalTransactionsSeries.setArguments(new ChartStringData(months));
            totalTransactionsSeries.setValues(new ChartNumericData(transactions));
            totalTransactionsSeries.setAxisGroup(ChartAxisGroupType.SECONDARY);
            chart.getSeries().add(totalTransactionsSeries);

            if (SHOW_AXIS_TITLES) {
                chart.getArgumentAxis().setTitle(new ChartTitleOptions("Months"));
                chart.getValueAxis().setTitle(new ChartTitleOptions("Units"));
            }

            chart.getSecondaryArgumentAxis().setVisible(false);
            chart.getSecondaryValueAxis().setPosition(AxisPositionType.RIGHT);

            OutlineStyle gridLineStyle = new OutlineStyle();
            gridLineStyle.setFill(new SolidFill(Color.getBlack()));
            ChartLineOptions gridLineOptions = new ChartLineOptions();
            gridLineOptions.setOutlineStyle(gridLineStyle);
            ChartLineOptions gridLines = SHOW_GRID_LINES ? gridLineOptions : null;
            chart.getValueAxis().setMajorGridlines(gridLines);
            chart.getArgumentAxis().setMinorGridlines(gridLines);
            chart.getSecondaryValueAxis().setMajorGridlines(gridLines);
            chart.getSecondaryArgumentAxis().setMinorGridlines(gridLines);

            chart.setStyle(CHART_STYLE);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (IS_PDF_OUTPUT)
                presentation.exportToPdf(stream);
            else
                presentation.save(stream, DocumentFormat.PPTX);
            return stream;
        }
    }
}
