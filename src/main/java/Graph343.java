import com.google.common.base.Functions;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.algorithms.shortestpath.MinimumSpanningForest2;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.visualization.*;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import javax.swing.*;

public class Graph343 {

    public static JComponent node(List<String> list) {
        Graph graph = new DirectedSparseGraph<>();

        String mainNode = list.get(0).replaceAll("\\^.+", "");

        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            String[] pair = list.get(i).split("\\^");
            graph.addEdge(count++, pair[0], pair[1]);
        }

        MinimumSpanningForest2<String, Number> prim =
                new MinimumSpanningForest2(graph,
                        new DelegateForest(), DelegateTree.getFactory(),
                        Functions.constant(1.0));

        StaticLayout<String, Number> layout = new StaticLayout(graph, new RadialTreeLayout(prim.getForest()));

        VisualizationViewer visualizationViewer = new VisualizationViewer(
                new DefaultVisualizationModel(layout, new Dimension(720, 610)));
        visualizationViewer.getRenderContext().setEdgeShapeTransformer(EdgeShape.line(graph));
        visualizationViewer.setVertexToolTipTransformer(new ToStringLabeller());
        visualizationViewer.getRenderContext().setVertexFillPaintTransformer(v -> {
            String str = v.toString();
            if (str.equals(mainNode)) return Color.BLUE;
            if (str.startsWith("F ")) return Color.GREEN;
            if (str.startsWith("V ")) return Color.YELLOW;
            return Color.RED;
        });

        Rings rings = new Rings(new RadialTreeLayout(prim.getForest()), graph, visualizationViewer);
        visualizationViewer.addPreRenderPaintable(rings);

        return visualizationViewer;
    }

    static class Rings implements VisualizationServer.Paintable {

        Collection<Double> depths;

        RadialTreeLayout radialLayout;
        Graph<String, Number> graph;
        VisualizationViewer vv;

        public Rings(RadialTreeLayout radialLayout, Graph<String, Number> graph, VisualizationViewer vv) {
            this.radialLayout = radialLayout;
            this.graph = graph;
            this.vv = vv;
            depths = getDepths();
        }

        private Collection<Double> getDepths() {
            Set<Double> depths = new HashSet<Double>();
            Map<String,PolarPoint> polarLocations = radialLayout.getPolarLocations();
            for(String v : graph.getVertices()) {
                PolarPoint pp = polarLocations.get(v);
                depths.add(pp.getRadius());
            }
            return depths;
        }

        public void paint(Graphics g) {
            g.setColor(Color.lightGray);

            Graphics2D g2d = (Graphics2D)g;
            Point2D center = radialLayout.getCenter();

            Ellipse2D ellipse = new Ellipse2D.Double();
            for(double d : depths) {
                ellipse.setFrameFromDiagonal(center.getX()-d, center.getY()-d,
                        center.getX()+d, center.getY()+d);
                Shape shape = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).transform(ellipse);
                g2d.draw(shape);
            }
        }

        public boolean useTransform() {
            return true;
        }
    }

}
