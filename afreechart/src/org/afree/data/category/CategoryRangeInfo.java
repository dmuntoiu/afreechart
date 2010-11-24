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
 * ----------------------
 * CategoryRangeInfo.java
 * ----------------------
 * (C) Copyright 2010, by Icom Systech Co., Ltd.
 * (C) Copyright 2009, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Sato Yoshiaki (for Icom Systech Co., Ltd);
 *                   Niwano Masayoshi;
 *
 * Changes
 * -------
 * 27-Mar-2009 : Version 1 (DG);
 *
 * ------------- AFREECHART 0.0.1 ---------------------------------------------
 * 19-Nov-2010 : port JFreeChart 1.0.13 to Android as "AFreeChart"
 */

package org.afree.data.category;

import java.util.List;

import org.afree.data.Range;

/**
 * An interface that can (optionally) be implemented by a dataset to assist in
 * determining the minimum and maximum y-values.
 * 
 * @since JFreeChart 1.0.13
 */
public interface CategoryRangeInfo {

    /**
     * Returns the range of the values in this dataset's range.
     * 
     * @param visibleSeriesKeys
     *            the keys of the visible series.
     * @param includeInterval
     *            a flag that determines whether or not the y-interval is taken
     *            into account.
     * 
     * @return The range (or <code>null</code> if the dataset contains no
     *         values).
     */
    public Range getRangeBounds(List visibleSeriesKeys, boolean includeInterval);

}
