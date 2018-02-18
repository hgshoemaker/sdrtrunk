/*******************************************************************************
 * sdr-trunk
 * Copyright (C) 2014-2018 Dennis Sheirer
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by  the Free Software Foundation, either version 3 of the License, or  (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful,  but WITHOUT ANY WARRANTY; without even the implied
 * warranty of  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License  along with this program.
 * If not, see <http://www.gnu.org/licenses/>
 *
 ******************************************************************************/
package io.github.dsheirer.dsp.psk;

import io.github.dsheirer.sample.complex.Complex;

public interface ISymbolPhaseErrorCalculator
{
    /**
     * Adjusts the calculated symbol as necessary before calculating any phase error.  This might be useful for
     * differential encoding to remove a 45 degree rotational spin.
     * @param symbol to adjust
     */
    void adjust(Complex symbol);

    /**
     * Calculates the phase error of the symbol relative to an alignment for the constellation
     * @param symbol to calculate error
     * @return error value
     */
    float getPhaseError(Complex symbol);
}