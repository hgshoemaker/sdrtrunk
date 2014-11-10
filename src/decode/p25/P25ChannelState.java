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
 ******************************************************************************/
package decode.p25;

import alias.AliasList;
import audio.SquelchListener;
import audio.SquelchListener.SquelchState;
import controller.channel.ProcessingChain;
import controller.state.AuxChannelState;
import controller.state.ChannelState;

public class P25ChannelState extends ChannelState
{
	public P25ChannelState( ProcessingChain chain, AliasList aliasList )
	{
		super( chain, aliasList );
	}
	
	/**
	 * Make the ConventionalChannelState always unsquelched
	 */
	public void addListener( SquelchListener listener )
	{
		super.addListener( listener );
		
		super.setSquelchState( SquelchState.UNSQUELCH );
	}
	
	@Override
	public void setSquelchState( SquelchState state )
	{
		//do nothing ... we want the squelch state always unsquelched (for now)
	}
	@Override
    public String getActivitySummary()
    {
		StringBuilder sb = new StringBuilder();
		
		sb.append( "Activity Summary\n" );
		sb.append( "\tDecoder:\tP25\n\n" );
		
		for( AuxChannelState state: mAuxChannelStates )
		{
			sb.append( state.getActivitySummary() );
			sb.append( "\n\n" );
		}
		
		return sb.toString();
    }
}