// Copyright (C) 2005 Mila NetWhistler.  All rights reserved.
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.                                                            
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//       
// For more information contact: 
//      Mila NetWhistler        <netwhistler@gmail.com>
//      http://www.netwhistler.spb.ru/
package nnm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import nnm.inet.Service;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.TextAnchor;

public class GraphsPanel extends JPanel {

	private static JPanel stateP;

	private static JPanel responseP;

	private static DefaultCategoryDataset dataset;

	public GraphsPanel() {
		// super(new FlowLayout());
		final JPanel container = new JPanel();
		container.setLayout(new GridBagLayout());

		JPanel textP = new JPanel();
		textP.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		textP.setLayout(new FlowLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(1, 1, 1, 1);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		// state
		stateP = new JPanel();
		stateP.setLayout(new BorderLayout());
		stateP.add(createStateGraph());
		stateP.setPreferredSize(new Dimension(600, 200));
		stateP.setMaximumSize(new Dimension(600, 200));
		// response
		responseP = new JPanel();
		responseP.setLayout(new BorderLayout());
		responseP.add(createResponseGraph());
		responseP.setPreferredSize(new Dimension(600, 200));
		responseP.setMaximumSize(new Dimension(600, 200));
		// end graphs
		c.gridx = 0;
		c.gridy = 0;
		container.add(Box.createVerticalStrut(20), c);
		c.gridx = 0;
		c.gridy = 2;
		container.add(stateP, c);
		c.gridx = 0;
		c.gridy = 4;
		container.add(Box.createVerticalStrut(20), c);
		c.gridx = 0;
		c.gridy = 6;
		container.add(responseP, c);
		add(container, BorderLayout.CENTER);

	}

	public void Refresh() {
		stateP.removeAll();
		stateP.add(createStateGraph());
		stateP.updateUI();
		responseP.removeAll();
		responseP.add(createResponseGraph());
		responseP.updateUI();
	}

	public static JPanel createStateGraph() {
		JPanel panel = new JPanel(new GridLayout(1, 2));
		// panel.setPreferredSize(new java.awt.Dimension(800, 600));
		DefaultPieDataset dataset = new DefaultPieDataset();
		double bad = 0;
		double warn = 0;
		double all = 0;
		for (int i = 0; i < Graph.nodes.size(); i++) {
			Node aNode = (Node) Graph.nodes.get(i);
			if (aNode.getnodeType().equals("hub")
					|| aNode.getnodeType().equals("network-cloud")) {
				;
			} else {
				all++;
				if (!aNode.getBadStatus()) {
					int badserv = 0;
					for (int s = 0; s < aNode.getCheckPorts().size(); s++) {
						Service t = (Service) aNode.getCheckPorts().get(s);
						if (!t.getStatus())
							badserv = 1;
					}
					if (badserv == 1)
						warn++;
				} else {
					bad++;
				}

			}
		}

		dataset.setValue("Responding (OK)", (all - bad - warn));
		dataset.setValue("With failed service (WARNING)", warn);
		dataset.setValue("Unresponding Nodes (DOWN)", bad);
		//

		final JFreeChart chart = ChartFactory.createPieChart("", // chart
				// title
				dataset, // data
				true, // include legend
				true, false);
		TextTitle t = new TextTitle("Nodes State Summary");
		t.setFont(NetworkManagerGUI.baseFont);
		chart.setTitle(t);
		chart.setNotify(true);
		final PiePlot plot = (PiePlot) chart.getPlot();
		plot.setLabelFont(NetworkManagerGUI.baseFont);
		plot.setNoDataMessage("No data available");
		plot.setCircular(false);
		plot.setLabelLinkPaint(Color.red);
		plot.setLabelGap(0.02);
		// plot.setLabelGenerator(new StandardPieItemLabelGenerator("{2}%"));
		plot.setSectionPaint(0, Color.green);
		plot.setSectionPaint(1, Color.orange);
		plot.setSectionPaint(2, Color.red);
		chart.setBorderVisible(true);
		chart.setBackgroundPaint(Color.WHITE);
		ChartPanel chartPanel2 = new ChartPanel(chart);
		panel.add(chartPanel2);

		return panel;
	}

	public JPanel createResponseGraph() {
		// refresher.terminate();
		JPanel panel = new JPanel(new GridLayout(1, 2));
		// panel.setPreferredSize(new java.awt.Dimension(400, 300));
		dataset = new DefaultCategoryDataset();
		Vector tmpNodes = new Vector();
		final double[] da = new double[Graph.nodes.size()];
		for (int i = 0; i < Graph.nodes.size(); i++) {
			Node sNode = (Node) Graph.nodes.get(i);
			if (!sNode.getBadStatus() && sNode.getMonitor()) {
				if (NetworkManagerGUI.textHasContent(sNode.getResponse())) {
					String[] rtmp = sNode.getResponse().split(" ");
					tmpNodes.add(rtmp[0] + ":" + sNode.getIP());
					double tm = Double.parseDouble(rtmp[0]);
					da[i] = tm;
				}
			}
		}
		Arrays.sort(da);
		double sum = 0;
		double tmpd = 0;
		int n = 0;
		for (int i = 0; i < da.length; i++) {
			n = i;
			n++;
			sum += da[i];
		}
		double sred = sum / n;
		for (int i = da.length - 1; i >= 0; i--) {
			if (i == da.length - 10)
				break;
			if (da[i] > sred) {
				tmpd = da[i];
				for (int q = 0; q < tmpNodes.size(); q++) {
					String tmp = (String) tmpNodes.get(q);
					String[] s = tmp.split(":");
					if (tmpd == Double.parseDouble(s[0])) {
						dataset.addValue(tmpd, "", s[1]);
					}
				}
			}
		}
		JFreeChart chart = ChartFactory.createBarChart("", null,
				"Response (ms)", dataset, PlotOrientation.HORIZONTAL, false,
				false, false);
		chart.setBorderVisible(true);
		TextTitle t = new TextTitle("Nodes With Longest Response Time");
		t.setFont(NetworkManagerGUI.baseFont);
		chart.setTitle(t);
		chart.setNotify(true);
		chart.setBackgroundPaint(Color.WHITE);
		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		plot.setDomainAxis(domainAxis);
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setUpperMargin(0.15);
		rangeAxis.setLowerMargin(0.15);
		plot.setRangeAxis(rangeAxis);
		plot.setNoDataMessage("No data available");

		final CategoryItemRenderer renderer = new CustomRenderer(new Paint[] {
				Color.red, Color.magenta,Color.pink, Color.orange,
				Color.yellow, Color.cyan, Color.green, Color.blue, Color.gray, 
				Color.black });
		// renderer.setLabelGenerator(new StandardCategoryLabelGenerator());
		renderer.setItemLabelsVisible(true);
		final ItemLabelPosition p = new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER,
				45.0);
		renderer.setPositiveItemLabelPosition(p);
		plot.setRenderer(renderer);

		//CategoryAxis axis=plot.getDomainAxis();
		//axis.setLowerMargin(0.15);
		//axis.setUpperMargin(0.15);
		// plot.setForegroundAlpha(0.6f);
		plot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
		ChartPanel chartPanel2 = new ChartPanel(chart);
		panel.add(chartPanel2);
		return panel;

	}

	class CustomRenderer extends BarRenderer {

		private Paint[] colors;

		public CustomRenderer(final Paint[] colors) {
			this.colors = colors;
		}

		public Paint getItemPaint(final int row, final int column) {
			return this.colors[column % this.colors.length];
		}
	}

}
