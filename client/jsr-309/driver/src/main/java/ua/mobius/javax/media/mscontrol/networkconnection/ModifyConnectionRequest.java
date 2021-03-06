/*
 * JBoss, Home of Professional Open Source
 * Copyright XXXX, Red Hat Middleware LLC, and individual contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */

/*
 * 15/07/13 - Change notice:
 * This file has been modified by Mobius Software Ltd.
 * For more information please visit http://www.mobius.ua
 */
package ua.mobius.javax.media.mscontrol.networkconnection;

import jain.protocol.ip.mgcp.message.ModifyConnection;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import ua.mobius.fsm.State;
import ua.mobius.fsm.TransitionHandler;

/**
 *
 * @author kulikov
 */
public class ModifyConnectionRequest implements TransitionHandler {
    private NetworkConnectionImpl connection;
    
    protected ModifyConnectionRequest(NetworkConnectionImpl connection) {
        this.connection = connection;
    }
    
    public void process(State state) {
        //prepear callID and endpointID parameters for request
        CallIdentifier callId = connection.getMediaSession().getCallID();
        EndpointIdentifier endpointID = connection.getEndpoint().getIdentifier();
        ConnectionIdentifier connectionID = connection.getConnectionID();
        
        //ask for new unique transaction handler
        int txHandle = connection.getMediaSession().getUniqueHandler();
        
        ModifyConnection req = new ModifyConnection(this, callId, endpointID, connectionID);
        req.setMode(ConnectionMode.Confrnce);
        req.setTransactionHandle(txHandle);
        

        try {
            req.setRemoteConnectionDescriptor(new ConnectionDescriptor(connection.sdpPortManager.remoteSdp.toString()));
        } catch (Exception e) {
        }
        
        //send request
        connection.getMediaSession().getDriver().attach(txHandle, new ModifyConnectionResponseHandler(connection));
        connection.getMediaSession().getDriver().send(req);
    }

}
