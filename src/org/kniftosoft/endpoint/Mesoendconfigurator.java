package org.kniftosoft.endpoint;

import javax.websocket.server.ServerEndpointConfig;

/**
 * 
 * @author julian
 * @category 
 * @param <T>
 */
public class Mesoendconfigurator<T> extends ServerEndpointConfig.Configurator{
	private static MesopotamiaEndpoint theMesopotamiaServerEndpoint = new MesopotamiaEndpoint();
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public T  getEndpointInstance(Class endpointClass) throws InstantiationException {
        if (MesopotamiaEndpoint.class.equals(endpointClass)) {
            return (T) theMesopotamiaServerEndpoint;
        } else {
            throw new InstantiationException();
        }
    }
	
}
