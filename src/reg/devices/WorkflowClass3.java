package reg.devices;

import org.iotivity.base.examples.SimpleClient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class WorkflowClass3 {

public static class DelInfo {
		
		
		@SerializedName("id")
		@Expose
		private String id;
		
		
		
		public String getId() {
		return id;
		}

		public void setId(String id) {
		this.id = id;
		}

		
		
		
	}
	
	public static class ResponseDelete {
		@SerializedName("message")
		@Expose
		private String message;

		public ResponseDelete() {
		}

		public ResponseDelete(String message) {
			this.message = message;
		}

		public void setmessage(String message) {
			this.message = message;
		}

		public String getmessage() {
			return message;
		}
	}
	
	
	public ResponseDelete parseResponse(DelInfo info){
		//System.out.println(DIR);
		SQLiteJDBC.delete(info.getId());
		SimpleClient.callDelete(info.getId());
		return new ResponseDelete("Device with ID " + info.getId() + " is deleted");
	}
}
