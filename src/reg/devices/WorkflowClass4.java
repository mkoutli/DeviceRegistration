package reg.devices;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import reg.devices.WorkflowClass.RegInfo;



public class WorkflowClass4 {
	
public static class DeviceInfo extends RegInfo{
		

		@SerializedName("id")
		@Expose
		private String id;
	
		DeviceInfo(){}
		
		DeviceInfo(String id, String name, String type, String address, String password, String manufacturer,
				String model) {
			super(name, type, address, password, manufacturer, model);
			this.id = id;
			
		}
		
		public String getid() {
			return id;
		}

		public void setid(String id) {
			this.id = id;
		}
		
		
		
	}

	public static class ResponseGet {
		@SerializedName("devices")
		@Expose
		private ArrayList<DeviceInfo> devices = new ArrayList<DeviceInfo>();

		public ResponseGet() {
		}

		public ResponseGet(ArrayList<DeviceInfo> devices) {
			this.devices = devices;
		}

		public void setDevices(ArrayList<DeviceInfo> devices) {
			this.devices = devices;
		}

		public ArrayList<DeviceInfo> getDevices() {
			return devices;
		}
	}
	
	
	public ResponseGet parseResponse(){
		//System.out.println(DIR);
		ArrayList<DeviceInfo> device_list = SQLiteJDBC.getAllDevices();
		//SimpleClient.callPost(info.getName(), info.getType(), info.getMacAddress(), info.getPass());
		ResponseGet response = new ResponseGet(device_list);
		
		
		return response;
	}
}
