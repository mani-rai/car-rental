package net.manirai.rental.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * 
 * @author Mani
 *
 */
public class ConflictException extends WebApplicationException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public ConflictException(Response resp) {
        super(resp);
    }

}
