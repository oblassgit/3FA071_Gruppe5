package dev.hv.rest.resource;
import dev.hv.Util;
import dev.hv.db.DatabaseCon;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("setupDB")
public class DbResource {

    private Util util = new Util();
    private DatabaseCon databaseCon = new DatabaseCon();
    
    @DELETE
    public Response setupDB() {
            databaseCon.openConnections(util.getProperties());
            
            databaseCon.removeAllTables(); //Löscht Customer und Reading
            databaseCon.createAllTables();
            System.out.println("..");
        
        return Response.ok().build();
    }
    
}
