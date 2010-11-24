/* ===========================================================
 * AFreeChart : a free chart library for Android(tm) platform.
 *              (based on JFreeChart and JCommon)
 * ===========================================================
 *
 * (C) Copyright 2010, by Icom Systech Co., Ltd.
 * (C) Copyright 2000-2009, by Object Refinery Limited and Contributors.
 *
 * Project Info:
 *    JFreeChart: http://www.jfree.org/jfreechart/index.html
 *    JCommon   : http://www.jfree.org/jcommon/index.html
 *    AFreeChart: http://code.google.com/p/afreechart/
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Android is a trademark of Google Inc.]
 *
 * ---------------------
 * AbstractRenderer.java
 * ---------------------
 * (C) Copyright 2010, by Icom Systech Co., Ltd.
 * (C) Copyright 2002-2009, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Nicolas Brodu;
 *                   Sato Yoshiaki (for Icom Systech Co., Ltd);
 *                   Niwano Masayoshi;
 *
 * Changes:
 * --------
 * 22-Aug-2002 : Version 1, draws code out of AbstractXYItemRenderer to share
 *               with AbstractCategoryItemRenderer (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 06-Nov-2002 : Moved to the com.jrefinery.chart.renderer package (DG);
 * 21-Nov-2002 : Added a paint table for the renderer to use (DG);
 * 17-Jan-2003 : Moved plot classes into a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 29-Apr-2003 : Added valueLabelFont and valueLabelPaint attributes, based on
 *               code from Arnaud Lelievre (DG);
 * 29-Jul-2003 : Amended code that doesn't compile with JDK 1.2.2 (DG);
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 15-Sep-2003 : Fixed serialization (NB);
 * 17-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 07-Oct-2003 : Moved PlotRenderingInfo into RendererState to allow for
 *               multiple threads using a single renderer (DG);
 * 20-Oct-2003 : Added missing setOutlinePaint() method (DG);
 * 23-Oct-2003 : Split item label attributes into 'positive' and 'negative'
 *               values (DG);
 * 26-Nov-2003 : Added methods to get the positive and negative item label
 *               positions (DG);
 * 01-Mar-2004 : Modified readObject() method to prevent null pointer exceptions
 *               after deserialization (DG);
 * 19-Jul-2004 : Fixed bug in getItemLabelFont(int, int) method (DG);
 * 04-Oct-2004 : Updated equals() method, eliminated use of NumberUtils,
 *               renamed BooleanUtils --> BooleanUtilities, ShapeUtils -->
 *               ShapeUtilities (DG);
 * 15-Mar-2005 : Fixed serialization of baseFillPaint (DG);
 * 16-May-2005 : Base outline stroke should never be null (DG);
 * 01-Jun-2005 : Added hasListener() method for unit testing (DG);
 * 08-Jun-2005 : Fixed equals() method to handle GradientPaint (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 02-Feb-2007 : Minor API doc update (DG);
 * 19-Feb-2007 : Fixes for clone() method (DG);
 * 28-Feb-2007 : Use cached event to signal changes (DG);
 * 19-Apr-2007 : Deprecated seriesVisible and seriesVisibleInLegend flags (DG);
 * 20-Apr-2007 : Deprecated paint, fillPaint, outlinePaint, stroke,
 *               outlineStroke, shape, itemLabelsVisible, itemLabelFont,
 *               itemLabelPaint, positiveItemLabelPosition,
 *               negativeItemLabelPosition and createEntities override
 *               fields (DG);
 * 13-Jun-2007 : Added new autoPopulate flags for core series attributes (DG);
 * 23-Oct-2007 : Updated lookup methods to better handle overridden
 *               methods (DG);
 * 04-Dec-2007 : Modified hashCode() implementation (DG);
 * 29-Apr-2008 : Minor API doc update (DG);
 * 17-Jun-2008 : Added legendShape, legendTextFont and legendTextPaint
 *               attributes (DG);
 * 18-Aug-2008 : Added clearSeriesPaints() and clearSeriesStrokes() (DG);
 * 28-Jan-2009 : Equals method doesn't test Shape equality correctly (DG);
 * 27-Mar-2009 : Added dataBoundsIncludesVisibleSeriesOnly attribute, and
 *               updated renderer events for series visibility changes (DG);
 * 01-Apr-2009 : Factored up the defaultEntityRadius field from the
 *               AbstractXYItemRenderer class (DG);
 *
 * ------------- AFREECHART 0.0.1 ---------------------------------------------
 * 19-Nov-2010 : port JFreeChart 1.0.13 to Android as "AFreeChart"
 */

package org.afree.chart.renderer;

import java.io.Serializable;


import org.afree.util.BooleanList;
import org.afree.util.EffectList;
import org.afree.util.ObjectList;
import org.afree.util.ObjectUtilities;
import org.afree.util.PaintTypeList;
import org.afree.util.PaintTypeUtilities;
import org.afree.util.ShapeList;
import org.afree.util.ShapeUtilities;
import org.afree.util.StrokeList;
import org.afree.ui.TextAnchor;
import org.afree.chart.HashUtilities;
import org.afree.chart.event.RendererChangeEvent;
import org.afree.chart.labels.ItemLabelAnchor;
import org.afree.chart.labels.ItemLabelPosition;
import org.afree.chart.plot.DrawingSupplier;
import org.afree.chart.plot.PlotOrientation;
import org.afree.chart.title.LegendTitle;
import org.afree.graphics.geom.Font;
import org.afree.graphics.geom.RectShape;
import org.afree.graphics.geom.Shape;
import org.afree.graphics.PaintType;
import org.afree.graphics.SolidColor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Typeface;

/**
 * Base class providing common services for renderers. Most methods that update
 * attributes of the renderer will fire a {@link RendererChangeEvent}, which
 * normally means the plot that owns the renderer will receive notification that
 * the renderer has been changed (the plot will, in turn, notify the chart).
 */
public abstract class AbstractRenderer implements Cloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -828267569428206075L;

    /** Zero represented as a <code>Double</code>. */
    public static final Double ZERO = new Double(0.0);

    /** The default paint. */
    public static final PaintType DEFAULT_PAINT = new SolidColor(Color.BLUE);

    /** The default outline paint. */
    public static final PaintType DEFAULT_OUTLINE_PAINT = new SolidColor(Color.GRAY);

    /** The default stroke. */
    public static final float DEFAULT_STROKE = 1.0f;

    /** The default outline stroke. */
    public static final float DEFAULT_OUTLINE_STROKE = 1.0f;

    /** The default shape. */
    public static final Shape DEFAULT_SHAPE = new RectShape(-3.0,
            -3.0, 6.0, 6.0);

    /** The default value label font. */
    public static final Font DEFAULT_VALUE_LABEL_FONT = new Font("SansSerif",
            Typeface.NORMAL, 10);

    /** The default value label paint. */
    public static final Paint DEFAULT_VALUE_LABEL_PAINT = new Paint(
            Paint.ANTI_ALIAS_FLAG);
    static {
        DEFAULT_VALUE_LABEL_PAINT.setColor(Color.BLACK);
    }

    /** A list of flags that controls whether or not each series is visible. */
    private BooleanList seriesVisibleList;

    /** The default visibility for each series. */
    private boolean baseSeriesVisible;

    /**
     * A list of flags that controls whether or not each series is visible in
     * the legend.
     */
    private BooleanList seriesVisibleInLegendList;

    /** The default visibility for each series in the legend. */
    private boolean baseSeriesVisibleInLegend;

    /** The paint list. */
    private PaintTypeList paintList;

    /**
     * A flag that controls whether or not the paintList is auto-populated in
     * the {@link #lookupSeriesPaintType(int)} method.
     * 
     * @since JFreeChart 1.0.6
     */
    private boolean autoPopulateSeriesPaint;

    /** The base paint. */
    private transient PaintType basePaintType;

    /** The fill paint list. */
    private PaintTypeList fillPaintList;

    /**
     * A flag that controls whether or not the fillPaintList is auto-populated
     * in the {@link #lookupSeriesFillPaintType(int)} method.
     * 
     * @since JFreeChart 1.0.6
     */
    private boolean autoPopulateSeriesFillPaint;

    /** The base fill paint. */
    private transient PaintType baseFillPaintType;

    /** The outline paint list. */
    private PaintTypeList outlinePaintList;

    /**
     * A flag that controls whether or not the outlinePaintList is
     * auto-populated in the {@link #lookupSeriesOutlinePaintType(int)} method.
     * 
     * @since JFreeChart 1.0.6
     */
    private boolean autoPopulateSeriesOutlinePaint;

    /** The base outline paint. */
    private transient PaintType baseOutlinePaintType;

    /** The stroke list. */
    private StrokeList strokeList;
    
    private EffectList effectList;

    /**
     * A flag that controls whether or not the strokeList is auto-populated in
     * the {@link #lookupSeriesStroke(int)} method.
     * 
     * @since JFreeChart 1.0.6
     */
    private boolean autoPopulateSeriesStroke;
    
    private boolean autoPopulateSeriesEffect;

    /** The base stroke. */
    private transient float baseStroke;
    
    private transient PathEffect baseEffect;

    /** The outline stroke list. */
    private StrokeList outlineStrokeList;
    
    private EffectList outlineEffectList;

    /** The base outline stroke. */
    private transient float baseOutlineStroke;

    /**
     * A flag that controls whether or not the outlineStrokeList is
     * auto-populated in the {@link #lookupSeriesOutlineStroke(int)} method.
     * 
     * @since JFreeChart 1.0.6
     */
    private boolean autoPopulateSeriesOutlineStroke;
    
    private boolean autoPopulateSeriesOutlineEffect;

    /** A shape list. */
    private ShapeList shapeList;

    /**
     * A flag that controls whether or not the shapeList is auto-populated in
     * the {@link #lookupSeriesShape(int)} method.
     * 
     * @since JFreeChart 1.0.6
     */
    private boolean autoPopulateSeriesShape;

    /** The base shape. */
    private transient Shape baseShape;

    /** Visibility of the item labels PER series. */
    private BooleanList itemLabelsVisibleList;

    /** The base item labels visible. */
    private Boolean baseItemLabelsVisible;

    /** The item label font list (one font per series). */
    private ObjectList itemLabelFontList;

    /** The base item label font. */
    private Font baseItemLabelFont;

    /** The item label paint list (one paint per series). */
    private PaintTypeList itemLabelPaintList;

    /** The base item label paint. */
    private transient PaintType baseItemLabelPaintType;

    /** The positive item label position (per series). */
    private ObjectList positiveItemLabelPositionList;

    /** The fallback positive item label position. */
    private ItemLabelPosition basePositiveItemLabelPosition;

    /** The negative item label position (per series). */
    private ObjectList negativeItemLabelPositionList;

    /** The fallback negative item label position. */
    private ItemLabelPosition baseNegativeItemLabelPosition;

    /** The item label anchor offset. */
    private double itemLabelAnchorOffset = 2.0;

    /**
     * Flags that control whether or not entities are generated for each series.
     * This will be overridden by 'createEntities'.
     */
    private BooleanList createEntitiesList;

    /**
     * The default flag that controls whether or not entities are generated.
     * This flag is used when both the above flags return null.
     */
    private boolean baseCreateEntities;

    /**
     * The per-series legend shape settings.
     * 
     * @since JFreeChart 1.0.11
     */
    private ShapeList legendShape;

    /**
     * The base shape for legend items. If this is <code>null</code>, the series
     * shape will be used.
     * 
     * @since JFreeChart 1.0.11
     */
    private transient Shape baseLegendShape;

    /**
     * The per-series legend text font.
     * 
     * @since JFreeChart 1.0.11
     */
    private ObjectList legendTextFont;

    /**
     * The base legend font.
     * 
     * @since JFreeChart 1.0.11
     */
    private Font baseLegendTextFont;

    /**
     * The per series legend text paint settings.
     * 
     * @since JFreeChart 1.0.11
     */
    private PaintTypeList legendTextPaint;

    /**
     * The default paint for the legend text items (if this is <code>null</code>
     * , the {@link LegendTitle} class will determine the text paint to use.
     * 
     * @since JFreeChart 1.0.11
     */
    private transient PaintType baseLegendTextPaintType;

    /**
     * A flag that controls whether or not the renderer will include the
     * non-visible series when calculating the data bounds.
     * 
     * @since JFreeChart 1.0.13
     */
    private boolean dataBoundsIncludesVisibleSeriesOnly = true;

    /** The default radius for the entity 'hotspot' */
    private int defaultEntityRadius;

    /**
     * Default constructor.
     */
    public AbstractRenderer() {

        // this.seriesVisible = null;
        this.seriesVisibleList = new BooleanList();
        this.baseSeriesVisible = true;

        // this.seriesVisibleInLegend = null;
        this.seriesVisibleInLegendList = new BooleanList();
        this.baseSeriesVisibleInLegend = true;

        // this.paint = null;
        this.paintList = new PaintTypeList();
        this.basePaintType = DEFAULT_PAINT;
        this.autoPopulateSeriesPaint = true;

        // this.fillPaint = null;
        this.fillPaintList = new PaintTypeList();
        this.baseFillPaintType = new SolidColor(Color.WHITE);
        this.autoPopulateSeriesFillPaint = false;

        // this.outlinePaint = null;
        this.outlinePaintList = new PaintTypeList();
        this.baseOutlinePaintType = DEFAULT_OUTLINE_PAINT;
        this.autoPopulateSeriesOutlinePaint = false;

        // this.stroke = null;
        this.strokeList = new StrokeList();
        this.effectList = new EffectList();
        this.baseStroke = DEFAULT_STROKE;
        this.baseEffect = null;
        this.autoPopulateSeriesStroke = true;
        this.autoPopulateSeriesEffect = true;

        // this.outlineStroke = null;
        this.outlineStrokeList = new StrokeList();
        this.outlineEffectList = new EffectList();
        this.baseOutlineStroke = DEFAULT_OUTLINE_STROKE;
        this.autoPopulateSeriesOutlineStroke = true;
        this.autoPopulateSeriesOutlineEffect = true;

        // this.shape = null;
        this.shapeList = new ShapeList();
        this.baseShape = DEFAULT_SHAPE;
        this.autoPopulateSeriesShape = true;

        // this.itemLabelsVisible = null;
        this.itemLabelsVisibleList = new BooleanList();
        this.baseItemLabelsVisible = Boolean.FALSE;

        // this.itemLabelFont = null;
        this.itemLabelFontList = new ObjectList();
        this.baseItemLabelFont = new Font("SansSerif", Typeface.NORMAL, 10);

        // this.itemLabelPaint = null;
        this.itemLabelPaintList = new PaintTypeList();
        this.baseItemLabelPaintType = new SolidColor(Color.BLACK);

        // this.positiveItemLabelPosition = null;
        this.positiveItemLabelPositionList = new ObjectList();
        this.basePositiveItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);

        // this.negativeItemLabelPosition = null;
        this.negativeItemLabelPositionList = new ObjectList();
        this.baseNegativeItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);

        // this.createEntities = null;
        this.createEntitiesList = new BooleanList();
        this.baseCreateEntities = true;

        this.defaultEntityRadius = 3;

        this.legendShape = new ShapeList();
        this.baseLegendShape = null;

        this.legendTextFont = new ObjectList();
        this.baseLegendTextFont = null;

        this.legendTextPaint = new PaintTypeList();
        this.baseLegendTextPaintType = null;

    }

    /**
     * Returns the drawing supplier from the plot.
     * 
     * @return The drawing supplier.
     */
    public abstract DrawingSupplier getDrawingSupplier();

    // SERIES VISIBLE (not yet respected by all renderers)

    /**
     * Returns a boolean that indicates whether or not the specified item should
     * be drawn (this is typically used to hide an entire series).
     * 
     * @param series
     *            the series index.
     * @param item
     *            the item index.
     * 
     * @return A boolean.
     */
    public boolean getItemVisible(int series, int item) {
        return isSeriesVisible(series);
    }

    /**
     * Returns a boolean that indicates whether or not the specified series
     * should be drawn.
     * 
     * @param series
     *            the series index.
     * 
     * @return A boolean.
     */
    public boolean isSeriesVisible(int series) {
        boolean result = this.baseSeriesVisible;

        Boolean b = this.seriesVisibleList.getBoolean(series);
        if (b != null) {
            result = b.booleanValue();
        }

        return result;
    }

    /**
     * Returns the flag that controls whether a series is visible.
     * 
     * @param series
     *            the series index (zero-based).
     * 
     * @return The flag (possibly <code>null</code>).
     * 
     * @see #setSeriesVisible(int, Boolean)
     */
    public Boolean getSeriesVisible(int series) {
        return this.seriesVisibleList.getBoolean(series);
    }

    /**
     * Sets the flag that controls whether a series is visible and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param visible
     *            the flag (<code>null</code> permitted).
     * 
     * @see #getSeriesVisible(int)
     */
    public void setSeriesVisible(int series, Boolean visible) {
        setSeriesVisible(series, visible, true);
    }

    /**
     * Sets the flag that controls whether a series is visible and, if
     * requested, sends a {@link RendererChangeEvent} to all registered
     * listeners.
     * 
     * @param series
     *            the series index.
     * @param visible
     *            the flag (<code>null</code> permitted).
     * @param notify
     *            notify listeners?
     * 
     * @see #getSeriesVisible(int)
     */
    public void setSeriesVisible(int series, Boolean visible, boolean notify) {
        this.seriesVisibleList.setBoolean(series, visible);

    }

    /**
     * Returns the base visibility for all series.
     * 
     * @return The base visibility.
     * 
     * @see #setBaseSeriesVisible(boolean)
     */
    public boolean getBaseSeriesVisible() {
        return this.baseSeriesVisible;
    }

    /**
     * Sets the base visibility and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     * 
     * @param visible
     *            the flag.
     * 
     * @see #getBaseSeriesVisible()
     */
    public void setBaseSeriesVisible(boolean visible) {
        // defer argument checking...
        setBaseSeriesVisible(visible, true);
    }

    /**
     * Sets the base visibility and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible
     *            the visibility.
     * @param notify
     *            notify listeners?
     * 
     * @see #getBaseSeriesVisible()
     */
    public void setBaseSeriesVisible(boolean visible, boolean notify) {
        this.baseSeriesVisible = visible;

    }

    // SERIES VISIBLE IN LEGEND (not yet respected by all renderers)

    /**
     * Returns <code>true</code> if the series should be shown in the legend,
     * and <code>false</code> otherwise.
     * 
     * @param series
     *            the series index.
     * 
     * @return A boolean.
     */
    public boolean isSeriesVisibleInLegend(int series) {
        boolean result = this.baseSeriesVisibleInLegend;

        Boolean b = this.seriesVisibleInLegendList.getBoolean(series);
        if (b != null) {
            result = b.booleanValue();
        }

        return result;
    }

    /**
     * Returns the flag that controls whether a series is visible in the legend.
     * This method returns only the "per series" settings - to incorporate the
     * override and base settings as well, you need to use the
     * {@link #isSeriesVisibleInLegend(int)} method.
     * 
     * @param series
     *            the series index (zero-based).
     * 
     * @return The flag (possibly <code>null</code>).
     * 
     * @see #setSeriesVisibleInLegend(int, Boolean)
     */
    public Boolean getSeriesVisibleInLegend(int series) {
        return this.seriesVisibleInLegendList.getBoolean(series);
    }

    /**
     * Sets the flag that controls whether a series is visible in the legend and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param visible
     *            the flag (<code>null</code> permitted).
     * 
     * @see #getSeriesVisibleInLegend(int)
     */
    public void setSeriesVisibleInLegend(int series, Boolean visible) {
        setSeriesVisibleInLegend(series, visible, true);
    }

    /**
     * Sets the flag that controls whether a series is visible in the legend
     * and, if requested, sends a {@link RendererChangeEvent} to all registered
     * listeners.
     * 
     * @param series
     *            the series index.
     * @param visible
     *            the flag (<code>null</code> permitted).
     * @param notify
     *            notify listeners?
     * 
     * @see #getSeriesVisibleInLegend(int)
     */
    public void setSeriesVisibleInLegend(int series, Boolean visible,
            boolean notify) {
        this.seriesVisibleInLegendList.setBoolean(series, visible);

    }

    /**
     * Returns the base visibility in the legend for all series.
     * 
     * @return The base visibility.
     * 
     * @see #setBaseSeriesVisibleInLegend(boolean)
     */
    public boolean getBaseSeriesVisibleInLegend() {
        return this.baseSeriesVisibleInLegend;
    }

    /**
     * Sets the base visibility in the legend and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible
     *            the flag.
     * 
     * @see #getBaseSeriesVisibleInLegend()
     */
    public void setBaseSeriesVisibleInLegend(boolean visible) {
        // defer argument checking...
        setBaseSeriesVisibleInLegend(visible, true);
    }

    /**
     * Sets the base visibility in the legend and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible
     *            the visibility.
     * @param notify
     *            notify listeners?
     * 
     * @see #getBaseSeriesVisibleInLegend()
     */
    public void setBaseSeriesVisibleInLegend(boolean visible, boolean notify) {
        this.baseSeriesVisibleInLegend = visible;

    }

    // PAINT

    /**
     * Returns the paint used to fill data items as they are drawn.
     * <p>
     * The default implementation passes control to the
     * <code>lookupSeriesPaint()</code> method. You can override this method if
     * you require different behaviour.
     * 
     * @param row
     *            the row (or series) index (zero-based).
     * @param column
     *            the column (or category) index (zero-based).
     * 
     * @return The paint (never <code>null</code>).
     */
    public PaintType getItemPaintType(int row, int column) {
        return lookupSeriesPaintType(row);
    }

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     * 
     * @param series
     *            the series index (zero-based).
     * 
     * @return The paint (never <code>null</code>).
     * 
     * @since JFreeChart 1.0.6
     */
    public PaintType lookupSeriesPaintType(int series) {

        // otherwise look up the paint list
        PaintType seriesPaint = getSeriesPaintType(series);
        if (seriesPaint == null && this.autoPopulateSeriesPaint) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                seriesPaint = supplier.getNextPaintType();
                setSeriesPaintType(series, seriesPaint, false);
            }
        }
        if (seriesPaint == null) {
            seriesPaint = this.basePaintType;
        }
        return seriesPaint;

    }

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     * 
     * @param series
     *            the series index (zero-based).
     * 
     * @return The paint (possibly <code>null</code>).
     * 
     * @see #setSeriesPaintType(int, Paint)
     */
    public PaintType getSeriesPaintType(int series) {
        return this.paintList.getPaintType(series);
    }

    /**
     * Sets the paint used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param paintType
     *            the paint (<code>null</code> permitted).
     * 
     * @see #getSeriesPaintType(int)
     */
    public void setSeriesPaintType(int series, PaintType paintType) {
        setSeriesPaintType(series, paintType, true);
    }

    /**
     * Sets the paint used for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index.
     * @param paintType
     *            the paint (<code>null</code> permitted).
     * @param notify
     *            notify listeners?
     * 
     * @see #getSeriesPaintType(int)
     */
    public void setSeriesPaintType(int series, PaintType paintType, boolean notify) {
        this.paintList.setPaintType(series, paintType);

    }

    /**
     * Clears the series paint settings for this renderer and, if requested,
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param notify
     *            notify listeners?
     * 
     * @since JFreeChart 1.0.11
     */
    public void clearSeriesPaints(boolean notify) {
        this.paintList.clear();

    }

    /**
     * Returns the base paint.
     * 
     * @return The base paint (never <code>null</code>).
     * 
     * @see #setBasePaintType(Paint)
     */
    public PaintType getBasePaintType() {
        return this.basePaintType;
    }

    /**
     * Sets the base paint and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     * 
     * @param paintType
     *            the paint (<code>null</code> not permitted).
     * 
     * @see #getBasePaintType()
     */
    public void setBasePaintType(PaintType paintType) {
        // defer argument checking...
        setBasePaintType(paintType, true);
    }

    /**
     * Sets the base paint and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param paintType
     *            the paint (<code>null</code> not permitted).
     * @param notify
     *            notify listeners?
     * 
     * @see #getBasePaintType()
     */
    public void setBasePaintType(PaintType paintType, boolean notify) {
        this.basePaintType = paintType;

    }

    /**
     * Returns the flag that controls whether or not the series paint list is
     * automatically populated when {@link #lookupSeriesPaintType(int)} is called.
     * 
     * @return A boolean.
     * 
     * @since JFreeChart 1.0.6
     * 
     * @see #setAutoPopulateSeriesPaint(boolean)
     */
    public boolean getAutoPopulateSeriesPaint() {
        return this.autoPopulateSeriesPaint;
    }

    /**
     * Sets the flag that controls whether or not the series paint list is
     * automatically populated when {@link #lookupSeriesPaintType(int)} is called.
     * 
     * @param auto
     *            the new flag value.
     * 
     * @since JFreeChart 1.0.6
     * 
     * @see #getAutoPopulateSeriesPaint()
     */
    public void setAutoPopulateSeriesPaint(boolean auto) {
        this.autoPopulateSeriesPaint = auto;
    }

    // // FILL PAINT //////////////////////////////////////////////////////////

    /**
     * Returns the paint used to fill data items as they are drawn. The default
     * implementation passes control to the {@link #lookupSeriesFillPaintType(int)}
     * method - you can override this method if you require different behaviour.
     * 
     * @param row
     *            the row (or series) index (zero-based).
     * @param column
     *            the column (or category) index (zero-based).
     * 
     * @return The paint (never <code>null</code>).
     */
    public PaintType getItemFillPaintType(int row, int column) {
        return lookupSeriesFillPaintType(row);
    }

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     * 
     * @param series
     *            the series (zero-based index).
     * 
     * @return The paint (never <code>null</code>).
     * 
     * @since JFreeChart 1.0.6
     */
    public PaintType lookupSeriesFillPaintType(int series) {

        // otherwise look up the paint table
        PaintType seriesFillPaintType = getSeriesFillPaintType(series);
        if (seriesFillPaintType == null && this.autoPopulateSeriesFillPaint) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                seriesFillPaintType = supplier.getNextFillPaintType();
                setSeriesFillPaintType(series, seriesFillPaintType, false);
            }
        }
        if (seriesFillPaintType == null) {
            seriesFillPaintType = this.baseFillPaintType;
        }
        return seriesFillPaintType;

    }

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     * 
     * @param series
     *            the series (zero-based index).
     * 
     * @return The paint (never <code>null</code>).
     * 
     * @see #setSeriesFillPaintType(int, Paint)
     */
    public PaintType getSeriesFillPaintType(int series) {
        return this.fillPaintList.getPaintType(series);
    }

    /**
     * Sets the paint used for a series fill and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param paint
     *            the paint (<code>null</code> permitted).
     * 
     * @see #getSeriesFillPaintType(int)
     */
    public void setSeriesFillPaintType(int series, PaintType paint) {
        setSeriesFillPaintType(series, paint, true);
    }

    /**
     * Sets the paint used to fill a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param paintType
     *            the paint (<code>null</code> permitted).
     * @param notify
     *            notify listeners?
     * 
     * @see #getSeriesFillPaintType(int)
     */
    public void setSeriesFillPaintType(int series, PaintType paintType, boolean notify) {
        this.fillPaintList.setPaintType(series, paintType);

    }

    /**
     * Returns the base fill paint.
     * 
     * @return The paint (never <code>null</code>).
     * 
     * @see #setBaseFillPaintType(Paint)
     */
    public PaintType getBaseFillPaintType() {
        return this.baseFillPaintType;
    }

    /**
     * Sets the base fill paint and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     * 
     * @param paintType
     *            the paint (<code>null</code> not permitted).
     * 
     * @see #getBaseFillPaintType()
     */
    public void setBaseFillPaintType(PaintType paintType) {
        // defer argument checking...
        setBaseFillPaintType(paintType, true);
    }

    /**
     * Sets the base fill paint and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param paintType
     *            the paint (<code>null</code> not permitted).
     * @param notify
     *            notify listeners?
     * 
     * @see #getBaseFillPaintType()
     */
    public void setBaseFillPaintType(PaintType paintType, boolean notify) {
        if (paintType == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseFillPaintType = paintType;

    }

    /**
     * Returns the flag that controls whether or not the series fill paint list
     * is automatically populated when {@link #lookupSeriesFillPaintType(int)} is
     * called.
     * 
     * @return A boolean.
     * 
     * @since JFreeChart 1.0.6
     * 
     * @see #setAutoPopulateSeriesFillPaint(boolean)
     */
    public boolean getAutoPopulateSeriesFillPaint() {
        return this.autoPopulateSeriesFillPaint;
    }

    /**
     * Sets the flag that controls whether or not the series fill paint list is
     * automatically populated when {@link #lookupSeriesFillPaintType(int)} is
     * called.
     * 
     * @param auto
     *            the new flag value.
     * 
     * @since JFreeChart 1.0.6
     * 
     * @see #getAutoPopulateSeriesFillPaint()
     */
    public void setAutoPopulateSeriesFillPaint(boolean auto) {
        this.autoPopulateSeriesFillPaint = auto;
    }

    // OUTLINE PAINT //////////////////////////////////////////////////////////

    /**
     * Returns the paint used to outline data items as they are drawn.
     * <p>
     * The default implementation passes control to the
     * {@link #lookupSeriesOutlinePaint} method. You can override this method if
     * you require different behaviour.
     * 
     * @param row
     *            the row (or series) index (zero-based).
     * @param column
     *            the column (or category) index (zero-based).
     * 
     * @return The paint (never <code>null</code>).
     */
    public PaintType getItemOutlinePaintType(int row, int column) {
        return lookupSeriesOutlinePaintType(row);
    }

    /**
     * Returns the paint used to outline an item drawn by the renderer.
     * 
     * @param series
     *            the series (zero-based index).
     * 
     * @return The paint (never <code>null</code>).
     * 
     * @since JFreeChart 1.0.6
     */
    public PaintType lookupSeriesOutlinePaintType(int series) {

        // otherwise look up the paint table
        PaintType seriesOutlinePaintType = getSeriesOutlinePaintType(series);
        if (seriesOutlinePaintType == null && this.autoPopulateSeriesOutlinePaint) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                seriesOutlinePaintType = supplier.getNextOutlinePaintType();
                setSeriesOutlinePaintType(series, seriesOutlinePaintType, false);
            }
        }
        if (seriesOutlinePaintType == null) {
            seriesOutlinePaintType = this.baseOutlinePaintType;
        }
        return seriesOutlinePaintType;

    }

    /**
     * Returns the paint used to outline an item drawn by the renderer.
     * 
     * @param series
     *            the series (zero-based index).
     * 
     * @return The paint (possibly <code>null</code>).
     * 
     * @see #setSeriesOutlinePaintType(int, Paint)
     */
    public PaintType getSeriesOutlinePaintType(int series) {
        return this.outlinePaintList.getPaintType(series);
    }

    /**
     * Sets the paint used for a series outline and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param paintType
     *            the paint (<code>null</code> permitted).
     * 
     * @see #getSeriesOutlinePaintType(int)
     */
    public void setSeriesOutlinePaintType(int series, PaintType paintType) {
        setSeriesOutlinePaintType(series, paintType, true);
    }

    /**
     * Sets the paint used to draw the outline for a series and, if requested,
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param paintType
     *            the paint (<code>null</code> permitted).
     * @param notify
     *            notify listeners?
     * 
     * @see #getSeriesOutlinePaintType(int)
     */
    public void setSeriesOutlinePaintType(int series, PaintType paintType, boolean notify) {
        this.outlinePaintList.setPaintType(series, paintType);

    }

    /**
     * Returns the base outline paint.
     * 
     * @return The paint (never <code>null</code>).
     * 
     * @see #setBaseOutlinePaintType(Paint)
     */
    public PaintType getBaseOutlinePaintType() {
        return this.baseOutlinePaintType;
    }

    /**
     * Sets the base outline paint and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     * 
     * @param paintType
     *            the paint (<code>null</code> not permitted).
     * 
     * @see #getBaseOutlinePaintType()
     */
    public void setBaseOutlinePaintType(PaintType paintType) {
        // defer argument checking...
        setBaseOutlinePaintType(paintType, true);
    }

    /**
     * Sets the base outline paint and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param paintType
     *            the paint (<code>null</code> not permitted).
     * @param notify
     *            notify listeners?
     * 
     * @see #getBaseOutlinePaintType()
     */
    public void setBaseOutlinePaintType(PaintType paintType, boolean notify) {
        if (paintType == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseOutlinePaintType = paintType;

    }

    /**
     * Returns the flag that controls whether or not the series outline paint
     * list is automatically populated when
     * {@link #lookupSeriesOutlinePaintType(int)} is called.
     * 
     * @return A boolean.
     * 
     * @since JFreeChart 1.0.6
     * 
     * @see #setAutoPopulateSeriesOutlinePaint(boolean)
     */
    public boolean getAutoPopulateSeriesOutlinePaint() {
        return this.autoPopulateSeriesOutlinePaint;
    }

    /**
     * Sets the flag that controls whether or not the series outline paint list
     * is automatically populated when {@link #lookupSeriesOutlinePaintType(int)} is
     * called.
     * 
     * @param auto
     *            the new flag value.
     * 
     * @since JFreeChart 1.0.6
     * 
     * @see #getAutoPopulateSeriesOutlinePaint()
     */
    public void setAutoPopulateSeriesOutlinePaint(boolean auto) {
        this.autoPopulateSeriesOutlinePaint = auto;
    }

    // STROKE

    /**
     * Returns the stroke used to draw data items.
     * <p>
     * The default implementation passes control to the getSeriesStroke method.
     * You can override this method if you require different behaviour.
     * 
     * @param row
     *            the row (or series) index (zero-based).
     * @param column
     *            the column (or category) index (zero-based).
     * 
     * @return The stroke (never <code>null</code>).
     */
    public Float getItemStroke(int row, int column) {
        return lookupSeriesStroke(row);
    }
    
    public PathEffect getItemEffect(int row, int column) {
        return lookupSeriesEffect(row);
    }

    /**
     * Returns the stroke used to draw the items in a series.
     * 
     * @param series
     *            the series (zero-based index).
     * 
     * @return The stroke (never <code>null</code>).
     * 
     * @since JFreeChart 1.0.6
     */
    public float lookupSeriesStroke(int series) {

        // otherwise look up the paint table
        Float result = getSeriesStroke(series);
        if (result==null && this.autoPopulateSeriesStroke) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextStroke();
                setSeriesStroke(series, result, false);
            }
        }
        if (result == null) {
            result = this.baseStroke;
        }
        return result;

    }
    
    public PathEffect lookupSeriesEffect(int series) {

        // otherwise look up the paint table
        PathEffect result = getSeriesEffect(series);
        if (result==null && this.autoPopulateSeriesEffect) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextEffect();
                setSeriesEffect(series, result, false);
            }
        }
        if (result == null) {
            result = this.baseEffect;
        }
        return result;

    }

    /**
     * Returns the stroke used to draw the items in a series.
     * 
     * @param series
     *            the series (zero-based index).
     * 
     * @return The stroke (possibly <code>null</code>).
     * 
     * @see #setSeriesStroke(int, Stroke)
     */
    public Float getSeriesStroke(int series) {
        return this.strokeList.getStroke(series);
    }
    
    public PathEffect getSeriesEffect(int series) {
        return this.effectList.getEffect(series);
    }

    /**
     * Sets the stroke used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param stroke
     *            the stroke (<code>null</code> permitted).
     * 
     * @see #getSeriesStroke(int)
     */
    public void setSeriesStroke(int series, Float stroke) {
        setSeriesStroke(series, stroke, true);
    }

    /**
     * Sets the stroke for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param stroke
     *            the stroke (<code>null</code> permitted).
     * @param notify
     *            notify listeners?
     * 
     * @see #getSeriesStroke(int)
     */
    public void setSeriesStroke(int series, float stroke, boolean notify) {
        this.strokeList.setStroke(series, stroke);

    }
    
    public void setSeriesEffect(int series, PathEffect effect, boolean notify) {
        this.effectList.setEffect(series, effect);

    }

    /**
     * Clears the series stroke settings for this renderer and, if requested,
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param notify
     *            notify listeners?
     * 
     * @since JFreeChart 1.0.11
     */
    public void clearSeriesStrokes(boolean notify) {
        this.strokeList.clear();

    }

    /**
     * Returns the base stroke.
     * 
     * @return The base stroke (never <code>null</code>).
     * 
     * @see #setBaseStroke(Stroke)
     */
    public Float getBaseStroke() {
        return this.baseStroke;
    }

    /**
     * Sets the base stroke and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     * 
     * @param stroke
     *            the stroke (<code>null</code> not permitted).
     * 
     * @see #getBaseStroke()
     */
    public void setBaseStroke(float stroke) {
        // defer argument checking...
        setBaseStroke(stroke, true);
    }

    /**
     * Sets the base stroke and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     * 
     * @param stroke
     *            the stroke (<code>null</code> not permitted).
     * 
     * @see #getBaseStroke()
     */
    public void setBaseStroke(Float stroke) {
        // defer argument checking...
        setBaseStroke(stroke, true);
    }
    
    /**
     * Sets the base stroke and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param stroke
     *            the stroke (<code>null</code> not permitted).
     * @param notify
     *            notify listeners?
     * 
     * @see #getBaseStroke()
     */
    public void setBaseStroke(float stroke, boolean notify) {

        this.baseStroke = stroke;

    }

    /**
     * Returns the flag that controls whether or not the series stroke list is
     * automatically populated when {@link #lookupSeriesStroke(int)} is called.
     * 
     * @return A boolean.
     * 
     * @since JFreeChart 1.0.6
     * 
     * @see #setAutoPopulateSeriesStroke(boolean)
     */
    public boolean getAutoPopulateSeriesStroke() {
        return this.autoPopulateSeriesStroke;
    }

    /**
     * Sets the flag that controls whether or not the series stroke list is
     * automatically populated when {@link #lookupSeriesStroke(int)} is called.
     * 
     * @param auto
     *            the new flag value.
     * 
     * @since JFreeChart 1.0.6
     * 
     * @see #getAutoPopulateSeriesStroke()
     */
    public void setAutoPopulateSeriesStroke(boolean auto) {
        this.autoPopulateSeriesStroke = auto;
    }

    // OUTLINE STROKE

    /**
     * Returns the stroke used to outline data items. The default implementation
     * passes control to the {@link #lookupSeriesOutlineStroke(int)} method. You
     * can override this method if you require different behaviour.
     * 
     * @param row
     *            the row (or series) index (zero-based).
     * @param column
     *            the column (or category) index (zero-based).
     * 
     * @return The stroke (never <code>null</code>).
     */
    public Float getItemOutlineStroke(int row, int column) {
        return lookupSeriesOutlineStroke(row);
    }

    /**
     * Returns the stroke used to outline the items in a series.
     * 
     * @param series
     *            the series (zero-based index).
     * 
     * @return The stroke (never <code>null</code>).
     * 
     * @since JFreeChart 1.0.6
     */
    public Float lookupSeriesOutlineStroke(int series) {

        // otherwise look up the stroke table
        float result = getSeriesOutlineStroke(series);
        if (this.autoPopulateSeriesOutlineStroke) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextOutlineStroke();
                setSeriesOutlineStroke(series, result, false);
            }
        }

        return result;

    }

    public PathEffect getSeriesOutlineEffect(int series) {
        return this.outlineEffectList.getEffect(series);
    }
    
    public PathEffect getItemOutlineEffect(int row, int column) {
        return lookupSeriesOutlineEffect(row);
    }

    /**
     * Returns the stroke used to outline the items in a series.
     * 
     * @param series
     *            the series (zero-based index).
     * 
     * @return The stroke (never <code>null</code>).
     * 
     * @since JFreeChart 1.0.6
     */
    public PathEffect lookupSeriesOutlineEffect(int series) {

        // otherwise look up the stroke table
        PathEffect result = getSeriesOutlineEffect(series);
        if (this.autoPopulateSeriesOutlineEffect) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextOutlineEffect();
                setSeriesOutlineEffect(series, result, false);
            }
        }

        return result;

    }
    
    /**
     * Returns the stroke used to outline the items in a series.
     * 
     * @param series
     *            the series (zero-based index).
     * 
     * @return The stroke (possibly <code>null</code>).
     * 
     * @see #setSeriesOutlineStroke(int, Stroke)
     */
    public Float getSeriesOutlineStroke(int series) {
        return this.outlineStrokeList.getStroke(series) == null ? 1
                : this.outlineStrokeList.getStroke(series);
    }

    /**
     * Sets the outline stroke used for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param stroke
     *            the stroke (<code>null</code> permitted).
     * 
     * @see #getSeriesOutlineStroke(int)
     */
    public void setSeriesOutlineStroke(int series, Float stroke) {
        setSeriesOutlineStroke(series, stroke, true);
    }

    /**
     * Sets the outline stroke for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index.
     * @param stroke
     *            the stroke (<code>null</code> permitted).
     * @param notify
     *            notify listeners?
     * 
     * @see #getSeriesOutlineStroke(int)
     */
    public void setSeriesOutlineStroke(int series, float stroke, boolean notify) {
        this.outlineStrokeList.setStroke(series, stroke);

    }

    public void setSeriesOutlineEffect(int series, PathEffect stroke) {
        setSeriesOutlineEffect(series, stroke, true);
    }

    /**
     * Sets the outline stroke for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index.
     * @param stroke
     *            the stroke (<code>null</code> permitted).
     * @param notify
     *            notify listeners?
     * 
     * @see #getSeriesOutlineStroke(int)
     */
    public void setSeriesOutlineEffect(int series, PathEffect stroke, boolean notify) {
        this.outlineEffectList.setEffect(series, stroke);

    }
    
    /**
     * Returns the base outline stroke.
     * 
     * @return The stroke (never <code>null</code>).
     * 
     * @see #setBaseOutlineStroke(Stroke)
     */
    public Float getBaseOutlineStroke() {
        return this.baseOutlineStroke;
    }

    /**
     * Sets the base outline stroke and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     * 
     * @param stroke
     *            the stroke (<code>null</code> not permitted).
     * 
     * @see #getBaseOutlineStroke()
     */
    public void setBaseOutlineStroke(Float stroke) {
        setBaseOutlineStroke(stroke, true);
    }

    /**
     * Sets the base outline stroke and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param stroke
     *            the stroke (<code>null</code> not permitted).
     * @param notify
     *            a flag that controls whether or not listeners are notified.
     * 
     * @see #getBaseOutlineStroke()
     */
    public void setBaseOutlineStroke(Float stroke, boolean notify) {

        this.baseOutlineStroke = stroke;

    }

    /**
     * Returns the flag that controls whether or not the series outline stroke
     * list is automatically populated when
     * {@link #lookupSeriesOutlineStroke(int)} is called.
     * 
     * @return A boolean.
     * 
     * @since JFreeChart 1.0.6
     * 
     * @see #setAutoPopulateSeriesOutlineStroke(boolean)
     */
    public boolean getAutoPopulateSeriesOutlineStroke() {
        return this.autoPopulateSeriesOutlineStroke;
    }

    /**
     * Sets the flag that controls whether or not the series outline stroke list
     * is automatically populated when {@link #lookupSeriesOutlineStroke(int)}
     * is called.
     * 
     * @param auto
     *            the new flag value.
     * 
     * @since JFreeChart 1.0.6
     * 
     * @see #getAutoPopulateSeriesOutlineStroke()
     */
    public void setAutoPopulateSeriesOutlineEffect(boolean auto) {
        this.autoPopulateSeriesOutlineEffect = auto;
    }
    

    public boolean getAutoPopulateSeriesOutlineEffect() {
        return this.autoPopulateSeriesOutlineStroke;
    }

    /**
     * Sets the flag that controls whether or not the series outline stroke list
     * is automatically populated when {@link #lookupSeriesOutlineStroke(int)}
     * is called.
     * 
     * @param auto
     *            the new flag value.
     * 
     * @since JFreeChart 1.0.6
     * 
     * @see #getAutoPopulateSeriesOutlineStroke()
     */
    public void setAutoPopulateSeriesOutlineStroke(boolean auto) {
        this.autoPopulateSeriesOutlineStroke = auto;
    }

    // SHAPE

    /**
     * Returns a shape used to represent a data item.
     * <p>
     * The default implementation passes control to the getSeriesShape method.
     * You can override this method if you require different behaviour.
     * 
     * @param row
     *            the row (or series) index (zero-based).
     * @param column
     *            the column (or category) index (zero-based).
     * 
     * @return The shape (never <code>null</code>).
     */
    public Shape getItemShape(int row, int column) {
        return lookupSeriesShape(row);
    }

    /**
     * Returns a shape used to represent the items in a series.
     * 
     * @param series
     *            the series (zero-based index).
     * 
     * @return The shape (never <code>null</code>).
     * 
     * @since JFreeChart 1.0.6
     */
    public Shape lookupSeriesShape(int series) {

        // otherwise look up the shape list
        Shape result = getSeriesShape(series);
        if (result == null && this.autoPopulateSeriesShape) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextShape();
                setSeriesShape(series, result, false);
            }
        }
        if (result == null) {
            result = this.baseShape;
        }
        return result;

    }

    /**
     * Returns a shape used to represent the items in a series.
     * 
     * @param series
     *            the series (zero-based index).
     * 
     * @return The shape (possibly <code>null</code>).
     * 
     * @see #setSeriesShape(int, Shape)
     */
    public Shape getSeriesShape(int series) {
        return this.shapeList.getShape(series);
    }

    /**
     * Sets the shape used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param shape
     *            the shape (<code>null</code> permitted).
     * 
     * @see #getSeriesShape(int)
     */
    public void setSeriesShape(int series, Shape shape) {
        setSeriesShape(series, shape, true);
    }

    /**
     * Sets the shape for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero based).
     * @param shape
     *            the shape (<code>null</code> permitted).
     * @param notify
     *            notify listeners?
     * 
     * @see #getSeriesShape(int)
     */
    public void setSeriesShape(int series, Shape shape, boolean notify) {
        this.shapeList.setShape(series, shape);

    }

    /**
     * Returns the base shape.
     * 
     * @return The shape (never <code>null</code>).
     * 
     * @see #setBaseShape(Shape)
     */
    public Shape getBaseShape() {
        return this.baseShape;
    }

    /**
     * Sets the base shape and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     * 
     * @param shape
     *            the shape (<code>null</code> not permitted).
     * 
     * @see #getBaseShape()
     */
    public void setBaseShape(Shape shape) {
        // defer argument checking...
        setBaseShape(shape, true);
    }

    /**
     * Sets the base shape and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param shape
     *            the shape (<code>null</code> not permitted).
     * @param notify
     *            notify listeners?
     * 
     * @see #getBaseShape()
     */
    public void setBaseShape(Shape shape, boolean notify) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        this.baseShape = shape;

    }

    /**
     * Returns the flag that controls whether or not the series shape list is
     * automatically populated when {@link #lookupSeriesShape(int)} is called.
     * 
     * @return A boolean.
     * 
     * @since JFreeChart 1.0.6
     * 
     * @see #setAutoPopulateSeriesShape(boolean)
     */
    public boolean getAutoPopulateSeriesShape() {
        return this.autoPopulateSeriesShape;
    }

    /**
     * Sets the flag that controls whether or not the series shape list is
     * automatically populated when {@link #lookupSeriesShape(int)} is called.
     * 
     * @param auto
     *            the new flag value.
     * 
     * @since JFreeChart 1.0.6
     * 
     * @see #getAutoPopulateSeriesShape()
     */
    public void setAutoPopulateSeriesShape(boolean auto) {
        this.autoPopulateSeriesShape = auto;
    }

    // ITEM LABEL VISIBILITY...

    /**
     * Returns <code>true</code> if an item label is visible, and
     * <code>false</code> otherwise.
     * 
     * @param row
     *            the row index (zero-based).
     * @param column
     *            the column index (zero-based).
     * 
     * @return A boolean.
     */
    public boolean isItemLabelVisible(int row, int column) {
        return isSeriesItemLabelsVisible(row);
    }

    /**
     * Returns <code>true</code> if the item labels for a series are visible,
     * and <code>false</code> otherwise.
     * 
     * @param series
     *            the series index (zero-based).
     * 
     * @return A boolean.
     */
    public boolean isSeriesItemLabelsVisible(int series) {

        // otherwise look up the boolean table
        Boolean b = this.itemLabelsVisibleList.getBoolean(series);
        if (b == null) {
            b = this.baseItemLabelsVisible;
        }
        if (b == null) {
            b = Boolean.FALSE;
        }
        return b.booleanValue();

    }

    /**
     * Sets a flag that controls the visibility of the item labels for a series,
     * and sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param visible
     *            the flag.
     */
    public void setSeriesItemLabelsVisible(int series, boolean visible) {
        setSeriesItemLabelsVisible(series, Boolean.valueOf(visible));
    }

    /**
     * Sets the visibility of the item labels for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param visible
     *            the flag (<code>null</code> permitted).
     */
    public void setSeriesItemLabelsVisible(int series, Boolean visible) {
        setSeriesItemLabelsVisible(series, visible, true);
    }

    /**
     * Sets the visibility of item labels for a series and, if requested, sends
     * a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param visible
     *            the visible flag.
     * @param notify
     *            a flag that controls whether or not listeners are notified.
     */
    public void setSeriesItemLabelsVisible(int series, Boolean visible,
            boolean notify) {
        this.itemLabelsVisibleList.setBoolean(series, visible);

    }

    /**
     * Returns the base setting for item label visibility. A <code>null</code>
     * result should be interpreted as equivalent to <code>Boolean.FALSE</code>.
     * 
     * @return A flag (possibly <code>null</code>).
     * 
     * @see #setBaseItemLabelsVisible(boolean)
     */
    public Boolean getBaseItemLabelsVisible() {
        // this should have been defined as a boolean primitive, because
        // allowing null values is a nuisance...but it is part of the final
        // API now, so we'll have to support it.
        return this.baseItemLabelsVisible;
    }

    /**
     * Sets the base flag that controls whether or not item labels are visible,
     * and sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible
     *            the flag.
     * 
     * @see #getBaseItemLabelsVisible()
     */
    public void setBaseItemLabelsVisible(boolean visible) {
        setBaseItemLabelsVisible(Boolean.valueOf(visible));
    }

    /**
     * Sets the base setting for item label visibility and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible
     *            the flag (<code>null</code> is permitted, and viewed as
     *            equivalent to <code>Boolean.FALSE</code>).
     */
    public void setBaseItemLabelsVisible(Boolean visible) {
        setBaseItemLabelsVisible(visible, true);
    }

    /**
     * Sets the base visibility for item labels and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible
     *            the flag (<code>null</code> is permitted, and viewed as
     *            equivalent to <code>Boolean.FALSE</code>).
     * @param notify
     *            a flag that controls whether or not listeners are notified.
     * 
     * @see #getBaseItemLabelsVisible()
     */
    public void setBaseItemLabelsVisible(Boolean visible, boolean notify) {
        this.baseItemLabelsVisible = visible;

    }

    // // ITEM LABEL FONT //////////////////////////////////////////////////////

    /**
     * Returns the font for an item label.
     * 
     * @param row
     *            the row index (zero-based).
     * @param column
     *            the column index (zero-based).
     * 
     * @return The font (never <code>null</code>).
     */
    public Font getItemLabelFont(int row, int column) {
        Font result = null;
        if (result == null) {
            result = getSeriesItemLabelFont(row);
            if (result == null) {
                result = this.baseItemLabelFont;
            }
        }
        return result;
    }

    /**
     * Returns the font for all the item labels in a series.
     * 
     * @param series
     *            the series index (zero-based).
     * 
     * @return The font (possibly <code>null</code>).
     * 
     * @see #setSeriesItemLabelFont(int, Font)
     */
    public Font getSeriesItemLabelFont(int series) {
        return (Font) this.itemLabelFontList.get(series);
    }

    /**
     * Sets the item label font for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param font
     *            the font (<code>null</code> permitted).
     * 
     * @see #getSeriesItemLabelFont(int)
     */
    public void setSeriesItemLabelFont(int series, Font font) {
        setSeriesItemLabelFont(series, font, true);
    }

    /**
     * Sets the item label font for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero based).
     * @param font
     *            the font (<code>null</code> permitted).
     * @param notify
     *            a flag that controls whether or not listeners are notified.
     * 
     * @see #getSeriesItemLabelFont(int)
     */
    public void setSeriesItemLabelFont(int series, Font font, boolean notify) {
        this.itemLabelFontList.set(series, font);

    }

    /**
     * Returns the base item label font (this is used when no other font setting
     * is available).
     * 
     * @return The font (<code>never</code> null).
     * 
     * @see #setBaseItemLabelFont(Font)
     */
    public Font getBaseItemLabelFont() {
        return this.baseItemLabelFont;
    }

    /**
     * Sets the base item label font and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     * 
     * @param font
     *            the font (<code>null</code> not permitted).
     * 
     * @see #getBaseItemLabelFont()
     */
    public void setBaseItemLabelFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        setBaseItemLabelFont(font, true);
    }

    /**
     * Sets the base item label font and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param font
     *            the font (<code>null</code> not permitted).
     * @param notify
     *            a flag that controls whether or not listeners are notified.
     * 
     * @see #getBaseItemLabelFont()
     */
    public void setBaseItemLabelFont(Font font, boolean notify) {
        this.baseItemLabelFont = font;

    }

    // // ITEM LABEL PAINT ////////////////////////////////////////////////////

    /**
     * Returns the paint used to draw an item label.
     * 
     * @param row
     *            the row index (zero based).
     * @param column
     *            the column index (zero based).
     * 
     * @return The paint (never <code>null</code>).
     */
    public PaintType getItemLabelPaintType(int row, int column) {
        PaintType result = null;
        if (result == null) {
            result = getSeriesItemLabelPaintType(row);
            if (result == null) {
                result = this.baseItemLabelPaintType;
            }
        }
        return result;
    }

    /**
     * Returns the paint used to draw the item labels for a series.
     * 
     * @param series
     *            the series index (zero based).
     * 
     * @return The paint (possibly <code>null<code>).
     * 
     * @see #setSeriesItemLabelPaintType(int, Paint)
     */
    public PaintType getSeriesItemLabelPaintType(int series) {
        return this.itemLabelPaintList.getPaintType(series);
    }

    /**
     * Sets the item label paint for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series (zero based index).
     * @param paintType
     *            the paint (<code>null</code> permitted).
     * 
     * @see #getSeriesItemLabelPaintType(int)
     */
    public void setSeriesItemLabelPaintType(int series, PaintType paintType) {
        setSeriesItemLabelPaintTypeType(series, paintType, true);
    }

    /**
     * Sets the item label paint for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero based).
     * @param paintType
     *            the paint (<code>null</code> permitted).
     * @param notify
     *            a flag that controls whether or not listeners are notified.
     * 
     * @see #getSeriesItemLabelPaintType(int)
     */
    public void setSeriesItemLabelPaintTypeType(int series, PaintType paintType, boolean notify) {
        this.itemLabelPaintList.setPaintType(series, paintType);

    }

    /**
     * Returns the base item label paint.
     * 
     * @return The paint (never <code>null<code>).
     * 
     * @see #setBaseItemLabelPaintType(Paint)
     */
    public PaintType getBaseItemLabelPaintType() {
        return this.baseItemLabelPaintType;
    }

    /**
     * Sets the base item label paint and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     * 
     * @param paintType
     *            the paint (<code>null</code> not permitted).
     * 
     * @see #getBaseItemLabelPaintType()
     */
    public void setBaseItemLabelPaintType(PaintType paintType) {
        // defer argument checking...
        setBaseItemLabelPaintType(paintType, true);
    }

    /**
     * Sets the base item label paint and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners..
     * 
     * @param paintType
     *            the paint (<code>null</code> not permitted).
     * @param notify
     *            a flag that controls whether or not listeners are notified.
     * 
     * @see #getBaseItemLabelPaintType()
     */
    public void setBaseItemLabelPaintType(PaintType paintType, boolean notify) {
        if (paintType == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseItemLabelPaintType = paintType;

    }

    // POSITIVE ITEM LABEL POSITION...

    /**
     * Returns the item label position for positive values.
     * 
     * @param row
     *            the row index (zero-based).
     * @param column
     *            the column index (zero-based).
     * 
     * @return The item label position (never <code>null</code>).
     * 
     * @see #getNegativeItemLabelPosition(int, int)
     */
    public ItemLabelPosition getPositiveItemLabelPosition(int row, int column) {
        return getSeriesPositiveItemLabelPosition(row);
    }

    /**
     * Returns the item label position for all positive values in a series.
     * 
     * @param series
     *            the series index (zero-based).
     * 
     * @return The item label position (never <code>null</code>).
     * 
     * @see #setSeriesPositiveItemLabelPosition(int, ItemLabelPosition)
     */
    public ItemLabelPosition getSeriesPositiveItemLabelPosition(int series) {

        // otherwise look up the position table
        ItemLabelPosition position = (ItemLabelPosition) this.positiveItemLabelPositionList
                .get(series);
        if (position == null) {
            position = this.basePositiveItemLabelPosition;
        }
        return position;

    }

    /**
     * Sets the item label position for all positive values in a series and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param position
     *            the position (<code>null</code> permitted).
     * 
     * @see #getSeriesPositiveItemLabelPosition(int)
     */
    public void setSeriesPositiveItemLabelPosition(int series,
            ItemLabelPosition position) {
        setSeriesPositiveItemLabelPosition(series, position, true);
    }

    /**
     * Sets the item label position for all positive values in a series and (if
     * requested) sends a {@link RendererChangeEvent} to all registered
     * listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param position
     *            the position (<code>null</code> permitted).
     * @param notify
     *            notify registered listeners?
     * 
     * @see #getSeriesPositiveItemLabelPosition(int)
     */
    public void setSeriesPositiveItemLabelPosition(int series,
            ItemLabelPosition position, boolean notify) {
        this.positiveItemLabelPositionList.set(series, position);

    }

    /**
     * Returns the base positive item label position.
     * 
     * @return The position (never <code>null</code>).
     * 
     * @see #setBasePositiveItemLabelPosition(ItemLabelPosition)
     */
    public ItemLabelPosition getBasePositiveItemLabelPosition() {
        return this.basePositiveItemLabelPosition;
    }

    /**
     * Sets the base positive item label position.
     * 
     * @param position
     *            the position (<code>null</code> not permitted).
     * 
     * @see #getBasePositiveItemLabelPosition()
     */
    public void setBasePositiveItemLabelPosition(ItemLabelPosition position) {
        // defer argument checking...
        setBasePositiveItemLabelPosition(position, true);
    }

    /**
     * Sets the base positive item label position and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position
     *            the position (<code>null</code> not permitted).
     * @param notify
     *            notify registered listeners?
     * 
     * @see #getBasePositiveItemLabelPosition()
     */
    public void setBasePositiveItemLabelPosition(ItemLabelPosition position,
            boolean notify) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        this.basePositiveItemLabelPosition = position;

    }

    // NEGATIVE ITEM LABEL POSITION...

    /**
     * Returns the item label position for negative values. This method can be
     * overridden to provide customisation of the item label position for
     * individual data items.
     * 
     * @param row
     *            the row index (zero-based).
     * @param column
     *            the column (zero-based).
     * 
     * @return The item label position (never <code>null</code>).
     * 
     * @see #getPositiveItemLabelPosition(int, int)
     */
    public ItemLabelPosition getNegativeItemLabelPosition(int row, int column) {
        return getSeriesNegativeItemLabelPosition(row);
    }

    /**
     * Returns the item label position for all negative values in a series.
     * 
     * @param series
     *            the series index (zero-based).
     * 
     * @return The item label position (never <code>null</code>).
     * 
     * @see #setSeriesNegativeItemLabelPosition(int, ItemLabelPosition)
     */
    public ItemLabelPosition getSeriesNegativeItemLabelPosition(int series) {

        // otherwise look up the position list
        ItemLabelPosition position = (ItemLabelPosition) this.negativeItemLabelPositionList
                .get(series);
        if (position == null) {
            position = this.baseNegativeItemLabelPosition;
        }
        return position;

    }

    /**
     * Sets the item label position for negative values in a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param position
     *            the position (<code>null</code> permitted).
     * 
     * @see #getSeriesNegativeItemLabelPosition(int)
     */
    public void setSeriesNegativeItemLabelPosition(int series,
            ItemLabelPosition position) {
        setSeriesNegativeItemLabelPosition(series, position, true);
    }

    /**
     * Sets the item label position for negative values in a series and (if
     * requested) sends a {@link RendererChangeEvent} to all registered
     * listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param position
     *            the position (<code>null</code> permitted).
     * @param notify
     *            notify registered listeners?
     * 
     * @see #getSeriesNegativeItemLabelPosition(int)
     */
    public void setSeriesNegativeItemLabelPosition(int series,
            ItemLabelPosition position, boolean notify) {
        this.negativeItemLabelPositionList.set(series, position);

    }

    /**
     * Returns the base item label position for negative values.
     * 
     * @return The position (never <code>null</code>).
     * 
     * @see #setBaseNegativeItemLabelPosition(ItemLabelPosition)
     */
    public ItemLabelPosition getBaseNegativeItemLabelPosition() {
        return this.baseNegativeItemLabelPosition;
    }

    /**
     * Sets the base item label position for negative values and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position
     *            the position (<code>null</code> not permitted).
     * 
     * @see #getBaseNegativeItemLabelPosition()
     */
    public void setBaseNegativeItemLabelPosition(ItemLabelPosition position) {
        setBaseNegativeItemLabelPosition(position, true);
    }

    /**
     * Sets the base negative item label position and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position
     *            the position (<code>null</code> not permitted).
     * @param notify
     *            notify registered listeners?
     * 
     * @see #getBaseNegativeItemLabelPosition()
     */
    public void setBaseNegativeItemLabelPosition(ItemLabelPosition position,
            boolean notify) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        this.baseNegativeItemLabelPosition = position;

    }

    /**
     * Returns the item label anchor offset.
     * 
     * @return The offset.
     * 
     * @see #setItemLabelAnchorOffset(double)
     */
    public double getItemLabelAnchorOffset() {
        return this.itemLabelAnchorOffset;
    }

    /**
     * Sets the item label anchor offset.
     * 
     * @param offset
     *            the offset.
     * 
     * @see #getItemLabelAnchorOffset()
     */
    public void setItemLabelAnchorOffset(double offset) {
        this.itemLabelAnchorOffset = offset;

    }

    /**
     * Returns a boolean that indicates whether or not the specified item should
     * have a chart entity created for it.
     * 
     * @param series
     *            the series index.
     * @param item
     *            the item index.
     * 
     * @return A boolean.
     */
    public boolean getItemCreateEntity(int series, int item) {

        Boolean b = getSeriesCreateEntities(series);
        if (b != null) {
            return b.booleanValue();
        } else {
            return this.baseCreateEntities;
        }

    }

    /**
     * Returns the flag that controls whether entities are created for a series.
     * 
     * @param series
     *            the series index (zero-based).
     * 
     * @return The flag (possibly <code>null</code>).
     * 
     * @see #setSeriesCreateEntities(int, Boolean)
     */
    public Boolean getSeriesCreateEntities(int series) {
        return this.createEntitiesList.getBoolean(series);
    }

    /**
     * Sets the flag that controls whether entities are created for a series,
     * and sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index (zero-based).
     * @param create
     *            the flag (<code>null</code> permitted).
     * 
     * @see #getSeriesCreateEntities(int)
     */
    public void setSeriesCreateEntities(int series, Boolean create) {
        setSeriesCreateEntities(series, create, true);
    }

    /**
     * Sets the flag that controls whether entities are created for a series
     * and, if requested, sends a {@link RendererChangeEvent} to all registered
     * listeners.
     * 
     * @param series
     *            the series index.
     * @param create
     *            the flag (<code>null</code> permitted).
     * @param notify
     *            notify listeners?
     * 
     * @see #getSeriesCreateEntities(int)
     */
    public void setSeriesCreateEntities(int series, Boolean create,
            boolean notify) {
        this.createEntitiesList.setBoolean(series, create);

    }

    /**
     * Returns the base visibility for all series.
     * 
     * @return The base visibility.
     * 
     * @see #setBaseCreateEntities(boolean)
     */
    public boolean getBaseCreateEntities() {
        return this.baseCreateEntities;
    }

    /**
     * Sets the base flag that controls whether entities are created for a
     * series, and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     * 
     * @param create
     *            the flag.
     * 
     * @see #getBaseCreateEntities()
     */
    public void setBaseCreateEntities(boolean create) {
        // defer argument checking...
        setBaseCreateEntities(create, true);
    }

    /**
     * Sets the base flag that controls whether entities are created and, if
     * requested, sends a {@link RendererChangeEvent} to all registered
     * listeners.
     * 
     * @param create
     *            the visibility.
     * @param notify
     *            notify listeners?
     * 
     * @see #getBaseCreateEntities()
     */
    public void setBaseCreateEntities(boolean create, boolean notify) {
        this.baseCreateEntities = create;

    }

    /**
     * Returns the radius of the circle used for the default entity area when no
     * area is specified.
     * 
     * @return A radius.
     * 
     * @see #setDefaultEntityRadius(int)
     */
    public int getDefaultEntityRadius() {
        return this.defaultEntityRadius;
    }

    /**
     * Sets the radius of the circle used for the default entity area when no
     * area is specified.
     * 
     * @param radius
     *            the radius.
     * 
     * @see #getDefaultEntityRadius()
     */
    public void setDefaultEntityRadius(int radius) {
        this.defaultEntityRadius = radius;
    }

    /**
     * Performs a lookup for the legend shape.
     * 
     * @param series
     *            the series index.
     * 
     * @return The shape (possibly <code>null</code>).
     * 
     * @since JFreeChart 1.0.11
     */
    public Shape lookupLegendShape(int series) {
        Shape result = getLegendShape(series);
        if (result == null) {
            result = this.baseLegendShape;
        }
        if (result == null) {
            result = lookupSeriesShape(series);
        }
        return result;
    }

    /**
     * Returns the legend shape defined for the specified series (possibly
     * <code>null</code>).
     * 
     * @param series
     *            the series index.
     * 
     * @return The shape (possibly <code>null</code>).
     * 
     * @see #lookupLegendShape(int)
     * 
     * @since JFreeChart 1.0.11
     */
    public Shape getLegendShape(int series) {
        return this.legendShape.getShape(series);
    }

    /**
     * Sets the shape used for the legend item for the specified series, and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index.
     * @param shape
     *            the shape (<code>null</code> permitted).
     * 
     * @since JFreeChart 1.0.11
     */
    public void setLegendShape(int series, Shape shape) {
        this.legendShape.setShape(series, shape);
    }

    /**
     * Returns the default legend shape, which may be <code>null</code>.
     * 
     * @return The default legend shape.
     * 
     * @since JFreeChart 1.0.11
     */
    public Shape getBaseLegendShape() {
        return this.baseLegendShape;
    }

    /**
     * Sets the default legend shape and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     * 
     * @param shape
     *            the shape (<code>null</code> permitted).
     * 
     * @since JFreeChart 1.0.11
     */
    public void setBaseLegendShape(Shape shape) {
        this.baseLegendShape = shape;
    }

    /**
     * Performs a lookup for the legend text font.
     * 
     * @param series
     *            the series index.
     * 
     * @return The font (possibly <code>null</code>).
     * 
     * @since JFreeChart 1.0.11
     */
    public Font lookupLegendTextFont(int series) {
        Font result = getLegendTextFont(series);
        if (result == null) {
            result = this.baseLegendTextFont;
        }
        return result;
    }

    /**
     * Returns the legend text font defined for the specified series (possibly
     * <code>null</code>).
     * 
     * @param series
     *            the series index.
     * 
     * @return The font (possibly <code>null</code>).
     * 
     * @see #lookupLegendTextFont(int)
     * 
     * @since JFreeChart 1.0.11
     */
    public Font getLegendTextFont(int series) {
        return (Font) this.legendTextFont.get(series);
    }

    /**
     * Sets the font used for the legend text for the specified series, and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index.
     * @param font
     *            the font (<code>null</code> permitted).
     * 
     * @since JFreeChart 1.0.11
     */
    public void setLegendTextFont(int series, Font font) {
        this.legendTextFont.set(series, font);
    }

    /**
     * Returns the default legend text font, which may be <code>null</code>.
     * 
     * @return The default legend text font.
     * 
     * @since JFreeChart 1.0.11
     */
    public Font getBaseLegendTextFont() {
        return this.baseLegendTextFont;
    }

    /**
     * Sets the default legend text font and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     * 
     * @param font
     *            the font (<code>null</code> permitted).
     * 
     * @since JFreeChart 1.0.11
     */
    public void setBaseLegendTextFont(Font font) {
        this.baseLegendTextFont = font;
    }

    /**
     * Performs a lookup for the legend text paint.
     * 
     * @param series
     *            the series index.
     * 
     * @return The paint (possibly <code>null</code>).
     * 
     * @since JFreeChart 1.0.11
     */
    public PaintType lookupLegendTextPaintType(int series) {
        PaintType result = getLegendTextPaint(series);
        if (result == null) {
            result = this.baseLegendTextPaintType;
        }
        return result;
    }

    /**
     * Returns the legend text paint defined for the specified series (possibly
     * <code>null</code>).
     * 
     * @param series
     *            the series index.
     * 
     * @return The paint (possibly <code>null</code>).
     * 
     * @see #lookupLegendTextPaintType(int)
     * 
     * @since JFreeChart 1.0.11
     */
    public PaintType getLegendTextPaint(int series) {
        return this.legendTextPaint.getPaintType(series);
    }

    /**
     * Sets the paint used for the legend text for the specified series, and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series
     *            the series index.
     * @param paintType
     *            the paint (<code>null</code> permitted).
     * 
     * @since JFreeChart 1.0.11
     */
    public void setLegendTextPaintType(int series, PaintType paintType) {
        this.legendTextPaint.setPaintType(series, paintType);
    }

    /**
     * Returns the default legend text paint, which may be <code>null</code>.
     * 
     * @return The default legend text paint.
     * 
     * @since JFreeChart 1.0.11
     */
    public PaintType getBaseLegendTextPaintType() {
        return this.baseLegendTextPaintType;
    }

    /**
     * Sets the default legend text paint and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param paintType
     *            the paint (<code>null</code> permitted).
     * 
     * @since JFreeChart 1.0.11
     */
    public void setBaseLegendTextPaintType(PaintType paintType) {
        this.baseLegendTextPaintType = paintType;
    }

    /**
     * Returns the flag that controls whether or not the data bounds reported by
     * this renderer will exclude non-visible series.
     * 
     * @return A boolean.
     * 
     * @since JFreeChart 1.0.13
     */
    public boolean getDataBoundsIncludesVisibleSeriesOnly() {
        return this.dataBoundsIncludesVisibleSeriesOnly;
    }

    /**
     * Sets the flag that controls whether or not the data bounds reported by
     * this renderer will exclude non-visible series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visibleOnly
     *            include only visible series.
     * 
     * @since JFreeChart 1.0.13
     */
    public void setDataBoundsIncludesVisibleSeriesOnly(boolean visibleOnly) {
        this.dataBoundsIncludesVisibleSeriesOnly = visibleOnly;
    }

    /** The adjacent offset. */
    private static final double ADJ = Math.cos(Math.PI / 6.0);

    /** The opposite offset. */
    private static final double OPP = Math.sin(Math.PI / 6.0);

    /**
     * Calculates the item label anchor point.
     * 
     * @param anchor
     *            the anchor.
     * @param x
     *            the x coordinate.
     * @param y
     *            the y coordinate.
     * @param orientation
     *            the plot orientation.
     * 
     * @return The anchor point (never <code>null</code>).
     */
    protected PointF calculateLabelAnchorPoint(ItemLabelAnchor anchor,
            double x, double y, PlotOrientation orientation) {
        PointF result = null;
        if (anchor == ItemLabelAnchor.CENTER) {
            result = new PointF((float)(x), (float)(y));
        } else if (anchor == ItemLabelAnchor.INSIDE1) {
            result = new PointF((float)(x + OPP * this.itemLabelAnchorOffset), (float)(y
                    - ADJ * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.INSIDE2) {
            result = new PointF((float)(x + ADJ * this.itemLabelAnchorOffset), (float)(y
                    - OPP * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.INSIDE3) {
            result = new PointF((float)(x + this.itemLabelAnchorOffset), (float)(y));
        } else if (anchor == ItemLabelAnchor.INSIDE4) {
            result = new PointF((float)(x + ADJ * this.itemLabelAnchorOffset), (float)(y
                    + OPP * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.INSIDE5) {
            result = new PointF((float)(x + OPP * this.itemLabelAnchorOffset), (float)(y
                    + ADJ * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.INSIDE6) {
            result = new PointF((float)(x), (float)(y + this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.INSIDE7) {
            result = new PointF((float)(x - OPP * this.itemLabelAnchorOffset), (float)(y
                    + ADJ * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.INSIDE8) {
            result = new PointF((float)(x - ADJ * this.itemLabelAnchorOffset), (float)(y
                    + OPP * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.INSIDE9) {
            result = new PointF((float)(x - this.itemLabelAnchorOffset), (float)(y));
        } else if (anchor == ItemLabelAnchor.INSIDE10) {
            result = new PointF((float)(x - ADJ * this.itemLabelAnchorOffset), (float)(y
                    - OPP * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.INSIDE11) {
            result = new PointF((float)(x - OPP * this.itemLabelAnchorOffset), (float)(y
                    - ADJ * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.INSIDE12) {
            result = new PointF((float)(x), (float)(y - this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.OUTSIDE1) {
            result = new PointF((float)(x + 2.0 * OPP
                    * this.itemLabelAnchorOffset), (float)(y - 2.0 * ADJ
                    * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.OUTSIDE2) {
            result = new PointF((float)(x + 2.0 * ADJ
                    * this.itemLabelAnchorOffset), (float)(y - 2.0 * OPP
                    * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            result = new PointF((float)(x + 2.0 * this.itemLabelAnchorOffset), (float)(y));
        } else if (anchor == ItemLabelAnchor.OUTSIDE4) {
            result = new PointF((float)(x + 2.0 * ADJ
                    * this.itemLabelAnchorOffset), (float)(y + 2.0 * OPP
                    * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.OUTSIDE5) {
            result = new PointF((float)(x + 2.0 * OPP
                    * this.itemLabelAnchorOffset), (float)(y + 2.0 * ADJ
                    * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            result = new PointF((float)(x), (float)(y + 2.0 * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.OUTSIDE7) {
            result = new PointF((float)(x - 2.0 * OPP
                    * this.itemLabelAnchorOffset), (float)(y + 2.0 * ADJ
                    * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.OUTSIDE8) {
            result = new PointF((float)(x - 2.0 * ADJ
                    * this.itemLabelAnchorOffset), (float)(y + 2.0 * OPP
                    * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            result = new PointF((float)(x - 2.0 * this.itemLabelAnchorOffset), (float)(y));
        } else if (anchor == ItemLabelAnchor.OUTSIDE10) {
            result = new PointF((float)(x - 2.0 * ADJ
                    * this.itemLabelAnchorOffset), (float)(y - 2.0 * OPP
                    * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.OUTSIDE11) {
            result = new PointF((float)(x - 2.0 * OPP
                    * this.itemLabelAnchorOffset), (float)(y - 2.0 * ADJ
                    * this.itemLabelAnchorOffset));
        } else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            result = new PointF((float)(x), (float)(y - 2.0 * this.itemLabelAnchorOffset));
        }
        return result;
    }

    /**
     * Tests this renderer for equality with another object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return <code>true</code> or <code>false</code>.
     */
    //TODO : It comment out when transplanting to android. 
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractRenderer)) {
            return false;
        }
        AbstractRenderer that = (AbstractRenderer) obj;
        if (this.dataBoundsIncludesVisibleSeriesOnly
                != that.dataBoundsIncludesVisibleSeriesOnly) {
            return false;
        }
        if (this.defaultEntityRadius != that.defaultEntityRadius) {
            return false;
        }
        /*
        if (!ObjectUtilities.equal(this.seriesVisible, that.seriesVisible)) {
            return false;
        }
        */
        if (!this.seriesVisibleList.equals(that.seriesVisibleList)) {
            return false;
        }
        if (this.baseSeriesVisible != that.baseSeriesVisible) {
            return false;
        }
        /*
        if (!ObjectUtilities.equal(this.seriesVisibleInLegend,
                that.seriesVisibleInLegend)) {
            return false;
        }
        */
        if (!this.seriesVisibleInLegendList.equals(
                that.seriesVisibleInLegendList)) {
            return false;
        }
        if (this.baseSeriesVisibleInLegend != that.baseSeriesVisibleInLegend) {
            return false;
        }
        /*
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        */
        if (!ObjectUtilities.equal(this.paintList, that.paintList)) {
            return false;
        }
        if (!PaintTypeUtilities.equal(this.basePaintType, that.basePaintType)) {
            return false;
        }
        /*
        if (!PaintUtilities.equal(this.fillPaint, that.fillPaint)) {
            return false;
        }
        */
        if (!ObjectUtilities.equal(this.fillPaintList, that.fillPaintList)) {
            return false;
        }
        if (!PaintTypeUtilities.equal(this.baseFillPaintType, that.baseFillPaintType)) {
            return false;
        }
        /*
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        */
        if (!ObjectUtilities.equal(this.outlinePaintList,
                that.outlinePaintList)) {
            return false;
        }
        if (!PaintTypeUtilities.equal(this.baseOutlinePaintType,
                that.baseOutlinePaintType)) {
            return false;
        }
        /*
        if (!ObjectUtilities.equal(this.stroke, that.stroke)) {
            return false;
        }
        */
        if (!ObjectUtilities.equal(this.strokeList, that.strokeList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseStroke, that.baseStroke)) {
            return false;
        }
        /*
        if (!ObjectUtilities.equal(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        */
        if (!ObjectUtilities.equal(this.outlineStrokeList,
                that.outlineStrokeList)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.baseOutlineStroke, that.baseOutlineStroke)
        ) {
            return false;
        }
        /*
        if (!ShapeUtilities.equal(this.shape, that.shape)) {
            return false;
        }
        */
        if (!ObjectUtilities.equal(this.shapeList, that.shapeList)) {
            return false;
        }
        if (!ShapeUtilities.equal(this.baseShape, that.baseShape)) {
            return false;
        }
        /*
        if (!ObjectUtilities.equal(this.itemLabelsVisible,
                that.itemLabelsVisible)) {
            return false;
        }
        */
        if (!ObjectUtilities.equal(this.itemLabelsVisibleList,
                that.itemLabelsVisibleList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseItemLabelsVisible,
                that.baseItemLabelsVisible)) {
            return false;
        }
        /*
        if (!ObjectUtilities.equal(this.itemLabelFont, that.itemLabelFont)) {
            return false;
        }
        */
        if (!ObjectUtilities.equal(this.itemLabelFontList,
                that.itemLabelFontList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseItemLabelFont,
                that.baseItemLabelFont)) {
            return false;
        }
        /*
        if (!PaintUtilities.equal(this.itemLabelPaint, that.itemLabelPaint)) {
            return false;
        }
        */
        if (!ObjectUtilities.equal(this.itemLabelPaintList,
                that.itemLabelPaintList)) {
            return false;
        }
        if (!PaintTypeUtilities.equal(this.baseItemLabelPaintType,
                that.baseItemLabelPaintType)) {
            return false;
        }
        /*
        if (!ObjectUtilities.equal(this.positiveItemLabelPosition,
                that.positiveItemLabelPosition)) {
            return false;
        }
        */
        if (!ObjectUtilities.equal(this.positiveItemLabelPositionList,
                that.positiveItemLabelPositionList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.basePositiveItemLabelPosition,
                that.basePositiveItemLabelPosition)) {
            return false;
        }
        /*
        if (!ObjectUtilities.equal(this.negativeItemLabelPosition,
                that.negativeItemLabelPosition)) {
            return false;
        }
        */
        if (!ObjectUtilities.equal(this.negativeItemLabelPositionList,
                that.negativeItemLabelPositionList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseNegativeItemLabelPosition,
                that.baseNegativeItemLabelPosition)) {
            return false;
        }
        if (this.itemLabelAnchorOffset != that.itemLabelAnchorOffset) {
            return false;
        }
        /*
        if (!ObjectUtilities.equal(this.createEntities, that.createEntities)) {
            return false;
        }
        */
        if (!ObjectUtilities.equal(this.createEntitiesList,
                that.createEntitiesList)) {
            return false;
        }
        if (this.baseCreateEntities != that.baseCreateEntities) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendShape, that.legendShape)) {
            return false;
        }
        if (!ShapeUtilities.equal(this.baseLegendShape,
                that.baseLegendShape)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendTextFont, that.legendTextFont)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseLegendTextFont,
                that.baseLegendTextFont)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendTextPaint,
                that.legendTextPaint)) {
            return false;
        }
        if (!PaintTypeUtilities.equal(this.baseLegendTextPaintType,
                that.baseLegendTextPaintType)) {
            return false;
        }
        return true;
    }
    
    /**
     * Returns a hashcode for the renderer.
     *
     * @return The hashcode.
     */
    public int hashCode() {
        int result = 193;
        result = HashUtilities.hashCode(result, this.seriesVisibleList);
        result = HashUtilities.hashCode(result, this.baseSeriesVisible);
        result = HashUtilities.hashCode(result, this.seriesVisibleInLegendList);
        result = HashUtilities.hashCode(result, this.baseSeriesVisibleInLegend);
        result = HashUtilities.hashCode(result, this.paintList);
        result = HashUtilities.hashCode(result, this.basePaintType);
        result = HashUtilities.hashCode(result, this.fillPaintList);
        result = HashUtilities.hashCode(result, this.baseFillPaintType);
        result = HashUtilities.hashCode(result, this.outlinePaintList);
        result = HashUtilities.hashCode(result, this.baseOutlinePaintType);
        result = HashUtilities.hashCode(result, this.strokeList);
        result = HashUtilities.hashCode(result, this.baseStroke);
        result = HashUtilities.hashCode(result, this.outlineStrokeList);
        result = HashUtilities.hashCode(result, this.baseOutlineStroke);
        // shapeList
        // baseShape
        result = HashUtilities.hashCode(result, this.itemLabelsVisibleList);
        result = HashUtilities.hashCode(result, this.baseItemLabelsVisible);
        // itemLabelFontList
        // baseItemLabelFont
        // itemLabelPaintList
        // baseItemLabelPaint
        // positiveItemLabelPositionList
        // basePositiveItemLabelPosition
        // negativeItemLabelPositionList
        // baseNegativeItemLabelPosition
        // itemLabelAnchorOffset
        // createEntityList
        // baseCreateEntities
        return result;
    }
    
    
}
