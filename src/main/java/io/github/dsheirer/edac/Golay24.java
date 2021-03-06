package io.github.dsheirer.edac;

import io.github.dsheirer.bits.BinaryMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*******************************************************************************
 *     SDR Trunk 
 *     Copyright (C) 2014 Dennis Sheirer
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 *     -----------------------------------------------------------------------
 *     Galois24 decoder based on Hank Wallace's tutorial/algorithm located at:
 *     http://www.aqdi.com/golay.htm
 ******************************************************************************/

/**
 * Galois 24/12/7 decoder
 */
public class Golay24
{
	private final static Logger mLog = LoggerFactory.getLogger( Golay24.class );

	/**
	 * Galois 24/12 checksums generated by:
	 * 
	 * CRCUtil.generate( 12, 11, 0xC75, 0x0, true );
	 */
	public static final int[] CHECKSUMS = new int[]
	{
	    0x63A, 0x31D, 0x7B4, 0x3DA, 0x1ED, 0x6CC, 0x366, 0x1B3, 
	    0x6E3, 0x54B, 0x49F, 0x475, 0x400, 0x200, 0x100, 0x080, 
	    0x040, 0x020, 0x010, 0x008, 0x004, 0x002, 0x001 
	};

	private static int calculateChecksum( BinaryMessage message, int startIndex )
	{
		int calculated = 0; //Starting value

		/* Iterate the set bits and XOR running checksum with lookup value */
		for (int i = message.nextSetBit( startIndex ); 
				 i >= startIndex && i < startIndex + 12; 
				 i = message.nextSetBit( i+1 ) ) 
		{
			calculated ^= CHECKSUMS[ i - startIndex ];
		}
		
		return calculated;
	}
	
	/**
	 * Performs error detection and returns a corrected copy of the 24-bit
	 * message that starts at the start index.
	 * @param message - source message containing startIndex + 24 bits length
	 * @param startIndex - start of the 24-bit galois 24 protected bit set
	 * @return - corrected 24-bit galois value
	 */
	public static BinaryMessage checkAndCorrect( BinaryMessage message, int startIndex )
	{
		boolean parityError = message.cardinality() % 2 != 0;
		
		int syndrome = getSyndrome( message, startIndex );
		
		/* No errors */
		if( syndrome == 0 )
		{
			if( parityError )
			{
				message.flip( startIndex + 23 );
			}

			message.setCRC( CRC.PASSED );
			
			return message;
		}

		/* Get original message value */
		int original = message.getInt( 0, 22 );
		
		int index = -1;
		int syndromeWeight = 3;
		int errors = 0;
		
		while( index < 23 )
		{
			if( index != -1 )
			{
				/* restore the previous flipped bit */
				if( index > 0 )
				{
					message.flip( index - 1 );
				}
				
				message.flip( index );
				
				syndromeWeight = 2;
			}
			
			syndrome = getSyndrome( message, startIndex );
			
			if( syndrome > 0 )
			{
				for( int i = 0; i < 23; i++ )
				{
				
					errors = Integer.bitCount( syndrome );
					
					if( errors <= syndromeWeight )
					{
						message.xor( 12, 11, syndrome );
						
						message.rotateRight( i, startIndex, startIndex + 22 );

						if( index >= 0 )
						{
							errors ++;
						}

						int corrected = message.getInt( 0, 22 );
						
						if( Integer.bitCount( original ^ corrected ) > 3 )
						{
							message.setCRC( CRC.FAILED_CRC );
							
							return message;
						}
						
						message.setCRC( CRC.PASSED );
						
						return message;
					}
					else
					{
						message.rotateLeft( startIndex, startIndex + 22 );
						syndrome = getSyndrome( message, startIndex );
					}
				}
				
				index++;
			}
		}

		message.setCRC( CRC.FAILED_CRC );
		
		return message;
	}

	private static int getSyndrome( BinaryMessage message, int startIndex )
	{
		int calculated = calculateChecksum( message, startIndex );
		
		int checksum = message.getInt( startIndex + 12, startIndex + 22 );

		return ( checksum ^ calculated );
	}
}
