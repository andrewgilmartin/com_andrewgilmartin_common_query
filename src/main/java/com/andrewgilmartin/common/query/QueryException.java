package com.andrewgilmartin.common.query;

import com.andrewgilmartin.common.exceptions.CommonException;

public class QueryException extends CommonException {

    public QueryException( Throwable cause, String message, Object ... parameters) {
        super( cause, message, parameters );
    }

    public QueryException( String message, Object ... parameters ) {
        super(null, message, parameters );
    }

    public QueryException( Throwable cause, String message ) {
        super( cause, message );
    }

    public QueryException( String message ) {
        super( (Throwable)null, message );
    }
}

// END
