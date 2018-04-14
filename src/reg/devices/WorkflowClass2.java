package reg.devices;

import org.iotivity.base.examples.SimpleClient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import reg.devices.WorkflowClass.RegInfo;


public class WorkflowClass2 {


	public static class UpInfo extends RegInfo{
		
		@SerializedName("id")
		@Expose
		private String id;
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		
		UpInfo(){
			super();
		}

		UpInfo(String id, String name, String type, String address, String password, String manufacturer, String model) {
			super(name, type, address, password, manufacturer, model);
			this.id = id;
		}

	}
	
	public static class ResponseUpdate {
		@SerializedName("message")
		@Expose
		private String message;

		public ResponseUpdate() {
		}

		public ResponseUpdate(String message) {
			this.message = message;
		}

		public void setmessage(String message) {
			this.message = message;
		}

		public String getmessage() {
			return message;
		}
	}
	
	
	public ResponseUpdate parseResponse(UpInfo info){
		//System.out.println(DIR);
		SQLiteJDBC.update(info);
		SimpleClient.callPut(info.getId(), info.getName(), info.getType(), info.getAddress(), info.getPassword(), info.getManufacturer(), info.getModel());
		return new ResponseUpdate("Update of device with id: " + info.getId() + " was successful");
	}
}
