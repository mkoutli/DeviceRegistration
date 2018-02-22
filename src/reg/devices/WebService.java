package reg.devices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import reg.devices.WorkflowClass;
import reg.devices.WorkflowClass.Response;
import reg.devices.WorkflowClass2.ResponseUpdate;
import reg.devices.WorkflowClass2.UpInfo;
import reg.devices.WorkflowClass3.DelInfo;
import reg.devices.WorkflowClass3.ResponseDelete;
import reg.devices.WorkflowClass4.ResponseGet;
import reg.devices.WorkflowClass.RegInfo;


@Path("/service")
public class WebService {

	/// Database functions
	@POST
    @Path("/devices")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerDevice(RegInfo info) throws Exception {
		WorkflowClass newClass = new WorkflowClass();
        Response response = newClass.parseResponse(info);
        return response;
    }
	
	@PUT
	@Path("/devices")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public ResponseUpdate updateDevice(UpInfo info) throws Exception {
		WorkflowClass2 newClass = new WorkflowClass2();
        ResponseUpdate response = newClass.parseResponse(info);
        return response;
    }
	
	@DELETE
	@Path("/devices")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public ResponseDelete removeDevice(DelInfo info) throws Exception {
		WorkflowClass3 newClass = new WorkflowClass3();
        ResponseDelete response = newClass.parseResponse(info);
        return response;
    }
	
	@GET
	@Path("/devices")
    @Produces(MediaType.APPLICATION_JSON)
	public ResponseGet getAllDevices() throws Exception {
		WorkflowClass4 newClass = new WorkflowClass4();
        ResponseGet response = newClass.parseResponse();
        return response;
    }
	
	
	// Add/remove tables in database
	@POST
    @Path("/table")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String createTable(@FormParam("name") String name, @FormParam("location") String location) throws Exception {
		SQLiteJDBC.createTable(name, location);
		return "Table with name " + name + " is created";
    }
	
	@DELETE
    @Path("/table")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteTable(@FormParam("name") String name) throws Exception {
		SQLiteJDBC.deleteTable(name);
		return "Table with name " + name + " is deleted";
    }
	
}
